import {
  VStack,
  Text,
  useBreakpointValue,
  Tabs,
  TabList,
  TabPanels,
  Tab,
  TabPanel,
  Menu,
  MenuButton,
  MenuList,
  MenuItem,
  IconButton,
  Icon,
} from "@chakra-ui/react";
import OrderHistory from "./OrderHistory";
import { OrderStatus } from "../../../enums/OrderStatus";
import { SortingOrder } from "./OrderHistory";
import { BsSortDown } from "react-icons/bs";
import { useState } from "react";

export interface ProfileHistoryProps {}

const ProfileHistory: React.FC<ProfileHistoryProps> = () => {
  const fontSize = useBreakpointValue({
    base: "24px",
    md: "32px",
    xl: "36px",
  })!;

  const iconSize = useBreakpointValue({
    base: "16px",
    md: "20px",
    lg: "24px",
    xl: "28px",
    "2xl": "32px",
  });

  const tabsFontSize = useBreakpointValue({
    base: "14px",
    md: "16px",
    lg: "18px",
    xl: "20px",
    "2xl": "22px",
  });

  const [sortingOrder, setSortingOrder] = useState<SortingOrder>(
    SortingOrder.TIME_DESCENDING
  );

  const [reloadCanceled, setReloadCanceled] = useState(false);

  return (
    <VStack w="100%" h="100%" spacing={0}>
      <Text
        textStyle="h3"
        color="primary.300"
        fontSize={fontSize}
        w="inherit"
        bg="purple.50"
        textAlign="center"
      >
        Orders & Returns
      </Text>
      <Tabs
        colorScheme="primary"
        w="inherit"
        h="inherit"
        display="flex"
        flexDirection="column"
      >
        <TabList
          boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
          justifyContent="center"
          bg="purple.50"
          position="relative"
        >
          {Object.values(OrderStatus)
            .filter((status) => isNaN(Number(status)))
            .map((status, index) => (
              <Tab key={index} fontSize={tabsFontSize} textStyle="p">
                {status}
              </Tab>
            ))}
          <Menu>
            <MenuButton
              as={IconButton}
              icon={<Icon as={BsSortDown} boxSize={iconSize} />}
              position="absolute"
              left={0}
              bottom={0}
              color="primary.400"
              backgroundColor="purple.50"
              _hover={{}}
              _active={{}}
            />
            <MenuList backgroundColor="primary.50" color="primary.400">
              {Object.values(SortingOrder)
                .filter((order) => isNaN(Number(order)))
                .map((order, index) => (
                  <MenuItem
                    key={index}
                    onClick={() => setSortingOrder(order)}
                    _hover={{ backgroundColor: "primary.100" }}
                  >
                    {order}
                  </MenuItem>
                ))}
            </MenuList>
          </Menu>
        </TabList>
        <TabPanels flexGrow={1} >
          {Object.values(OrderStatus)
            .filter((status) => isNaN(Number(status)))
            .map((status, index) => (
              <TabPanel key={index} h="100%">
                <OrderHistory
                  reloadCanceled={reloadCanceled}
                  setReloadCanceled={setReloadCanceled}
                  orderStatus={status}
                  sortingOrder={sortingOrder}
                />
              </TabPanel>
            ))}
        </TabPanels>
      </Tabs>
    </VStack>
  );
};

export default ProfileHistory;
