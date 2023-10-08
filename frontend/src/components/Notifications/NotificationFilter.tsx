import { Tab, TabList, Tabs } from "@chakra-ui/react";
import { FilterOption } from "./Notifications";
import { filterFontSize, filterWidth } from "./NotificationSizes";

type NotificationFilterProps = {
  setFilterOptions: React.Dispatch<React.SetStateAction<FilterOption>>;
};

const NotificationFilter = ({ setFilterOptions }: NotificationFilterProps) => {
  const notificationsReadFilterOptions: (keyof typeof FilterOption)[] = Object.keys(FilterOption) as (keyof typeof FilterOption)[];

  return (
    <Tabs
      variant="solid-rounded"
      colorScheme="primary"
      border="2px solid #9575CD"
      borderRight="0px"
      alignSelf="flex-start"
      onChange={(index) =>
        setFilterOptions(FilterOption[notificationsReadFilterOptions[index]])
      }
    >
      <TabList>
        {notificationsReadFilterOptions.map((option, index) => (
          <Tab
            key={index}
            rounded="0px"
            w={filterWidth}
            bg="primary.50"
            borderRight="2px solid #9575CD"
            textStyle="p"
            fontSize={filterFontSize}
          >
            {option}
          </Tab>
        ))}
      </TabList>
    </Tabs>
  );
};

export default NotificationFilter;
