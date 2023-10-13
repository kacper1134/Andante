import { HStack, Spacer, VStack, Text, Icon } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { IoIosNotifications } from "react-icons/io";
import PageChanger from "../Shop/ProductPage/ProductComments/PageChanger";
import NotificationFilter from "./NotificationFilter";
import Notification from "./Notification";
import { notificationHeaderSize, notificationWidth } from "./NotificationSizes";
import useAuthentication from "../../hooks/useAuthentication";
import { useSelector } from "react-redux";
import { RootState } from "../../store";
import { useGetByUserAndFilterOption } from "../../store/api/activitySlice";

export enum FilterOption {
  ALL = "ALL",
  READ = "READ",
  UNREAD = "UNREAD",
};

const Notifications = () => {
  useAuthentication("/home");
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const [filterOption, setFilterOptions] = useState<FilterOption>(FilterOption.ALL);
  const [page, setPage] = useState(0);
  const [numberOfPages, setNumberOfPages] = useState<number>(0);
  const pageSize = 8;
  const result = useGetByUserAndFilterOption(userDetails?.personal.emailAddress ?? "", page, pageSize, filterOption);

  useEffect(() => {
    setNumberOfPages(result.totalPages);
  }, [result]);

  useEffect(() => {
    setPage(0);
  }, [filterOption]);

  return (
    <VStack
      w={notificationWidth}
      h="100%"
      py="15px"
      alignSelf="center"
      spacing={4}
    >
      <HStack fontSize={notificationHeaderSize} color="primary.500">
        <Text textStyle="h1">Notifications</Text>
        <Icon as={IoIosNotifications} />
      </HStack>
      <HStack w="100%">
        <NotificationFilter setFilterOptions={setFilterOptions} />
        <Spacer />
      </HStack>
      {result.content.map((notification, index) => (
        <Notification key={index} notification={notification} />
      ))}
      {numberOfPages > 0 && <PageChanger page={page} setPage={setPage} numberOfPages={numberOfPages} />}
    </VStack>
  );
};

export default Notifications;
