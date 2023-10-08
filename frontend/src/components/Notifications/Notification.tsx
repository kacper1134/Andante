import {
  Box,
  Circle,
  HStack,
  Spacer,
  Text,
  Avatar,
  Center,
  Icon,
} from "@chakra-ui/react";
import { Domain, Priority } from "../../store/api/result/dto/activity/ActivityDTO";
import { motion } from "framer-motion";
import { DateTime } from "luxon";
import { useState } from "react";
import { AiOutlineDoubleRight, AiOutlineRight } from "react-icons/ai";
import { FaEquals } from "react-icons/fa";
import { ActivityDTO } from "../../store/api/result/dto/activity/ActivityDTO";
import NotificationModal from "./NotificationModal";
import {
  notificationFontSize,
  notificationIconSize,
  notificationSubFontSize,
} from "./NotificationSizes";
import { IconType } from "@react-icons/all-files";
import { useSelector } from "react-redux";
import { RootState } from "../../store";
import { useMarkAsReadMutation } from "../../store/api/activitySlice";

export enum NotificationPriority {
  HIGHEST,
  HIGH,
  MEDIUM,
  LOW,
  LOWEST,
}

type NotificationProps = {
  notification: ActivityDTO;
};

export interface NotificationTypeIcon {
  icon: IconType,
  color: string,
  rotate: string,
};

const notificationToImage = new Map<Domain, string>([
  [Domain.PRODUCT,"https://images.unsplash.com/photo-1505740106531-4243f3831c78?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80"],
  [Domain.FORUM, "https://images.unsplash.com/photo-1604881988758-f76ad2f7aac1?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1471&q=80"],
  [Domain.ORDER, "https://images.unsplash.com/photo-1579170053380-58064b2dee67?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1471&q=80"],
]);

const notificationTypeIcon = new Map<Priority, NotificationTypeIcon> ([
  [Priority.HIGHEST, { icon: AiOutlineDoubleRight, color: "red", rotate: "-90deg" }],
  [Priority.HIGH, { icon: AiOutlineRight, color: "red", rotate: "-90deg" }],
  [Priority.MEDIUM, { icon: FaEquals, color: "orange", rotate: "-0deg" }],
  [Priority.LOW, { icon: AiOutlineRight, color: "blue", rotate: "90deg" }],
  [Priority.LOWEST, { icon: AiOutlineDoubleRight, color: "blue", rotate: "90deg" }],
]);

const Notification = ({ notification }: NotificationProps) => {
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const notificationTime = DateTime.fromISO(notification.eventTimestamp).toLocaleString(DateTime.DATETIME_MED);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [markAsReadTrigger] = useMarkAsReadMutation();
  const isViewedByUser = notification.acknowledgedUsers.includes(userDetails?.personal.emailAddress ?? "");

  const onImageClickHandler = () => {
    setIsModalOpen(true);
  };

  const onNotificationClickHandler = () => {
    if (!isViewedByUser) {
      markAsReadTrigger({id: notification.id, email: userDetails!.personal.emailAddress});
    }
  };
  
  return (
    <HStack
      w="100%"
      boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
      p="10px"
      bg={isViewedByUser ? "primary.100" : "primary.50"}
      cursor="pointer"
      onClick={onNotificationClickHandler}
    >
      <Center w="10%" h="100%">
        <Avatar
          size={{ base: "sm", sm: "md" }}
          src={notificationToImage.get(notification.domain)}
          as={motion.div}
          whileHover={{
            scale: 1.1,
          }}
          onClick={onImageClickHandler}
        />
      </Center>
      <Box w="90%">
        <HStack w="100%" fontSize={notificationIconSize}>
          <Spacer />
          <Icon
            as={notificationTypeIcon.get(notification.priority)!.icon}
            transform={`rotate(${
              notificationTypeIcon.get(notification.priority)!.rotate
            })`}
            color={notificationTypeIcon.get(notification.priority)!.color}
          />
          <Circle
            bg={!isViewedByUser ? "primary.600" : ""}
            border="1px solid #5E35B1"
            size={notificationIconSize}
          />
        </HStack>
        <Text fontSize={notificationFontSize} textStyle="p">
          {notification.description}
        </Text>
        <Text as="sub" fontSize={notificationSubFontSize} textStyle="p">
          {notificationTime}
        </Text>
      </Box>
      <NotificationModal
        relatedId={notification.relatedId}
        type={notification.domain}
        isOpen={isModalOpen}
        setIsOpen={setIsModalOpen}
      />
    </HStack>
  );
};

export default Notification;
