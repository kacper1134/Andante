import {
  useBreakpointValue,
  VStack,
  Divider,
  Heading,
  HStack,
  Text,
  Button,
  useToast,
} from "@chakra-ui/react";
import { Dispatch, Fragment, SetStateAction, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../store";
import { OrderData, OrderDetails } from "./CartConfirmation";
import CartEntry from "./CartEntry";
import { cartActions } from "../../store/cart/cartSlice";
import ConfirmOrderModal from "./ConfirmOrderModal";

export interface DiscountCartSummaryProps {
    changeCurrentCartStep: Dispatch<SetStateAction<number>>,
    orderData: OrderData,
    headingSize: string,
}

const DiscountCartSummary: React.FC<DiscountCartSummaryProps> = ({
  changeCurrentCartStep,
  orderData,
  headingSize,
}) => {
  const dispatch = useDispatch();
  const toast = useToast();
  const cartItems = useSelector((state: RootState) => state.cart.cartItems);
  const currency = useSelector((state: RootState) => state.cart.currency);
  const totalPrice = cartItems
    .map((item) => item.quantity * item.variant.price)
    .reduce((prev, curr) => prev + curr, 0);
  const totalDiscount = 0;
  const finalPrice =
    totalPrice -
    totalDiscount +
    orderData.deliveryMethod.price +
    orderData.paymentMethod.cost;
  const orderDetails = useSelector((state: RootState) => state.cart);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const baseSize = useBreakpointValue({
    base: 340,
    md: 600,
    lg: 800,
    xl: 1000,
    "2xl": 1200,
  })!;

  function placeOrder() {
    dispatch(cartActions.updateDeliveryMethod(orderData.deliveryMethod));
    dispatch(cartActions.updatePaymentMethod(orderData.paymentMethod));

    if (arePersonalDetailsMissing(orderData.orderDetails)) {
      toast({
        title: "Missing Personal Details",
        description:
          "Required personal details are missing. Please fill them in before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else if (areDeliveryDetailsMissing(orderData.orderDetails)) {
      toast({
        title: "Missing Delivery Details",
        description:
          "Required delivery details are missing. Please fill them in before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else if (
      orderDetails.orderData.deliveryMethod.type === "Pickup Point" &&
      !orderDetails.deliveryPickupLocation
    ) {
      toast({
        title: "Missing Pickup Location",
        description:
          "Required pickup point location is missing. Please fill it in before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else if (!isPostalCodeValid(orderData.orderDetails.location.postalCode)) {
      toast({
        title: "Invalid Postal Code",
        description: "Provided postal code does not match required pattern of two digits followed by hyphen and three digits",
        status: "error",
        duration: 5000,
        isClosable: true,
      })
    } 
    else {
      setIsModalOpen(true);
    }
  }

  function arePersonalDetailsMissing(orderDetails: OrderDetails) {
    return (
      orderDetails.personal.name.length < 1 ||
      orderDetails.personal.surname.length < 1 ||
      orderDetails.personal.emailAddress.length < 1 ||
      orderDetails.personal.phoneNumber.length !== 9
    );
  }

  function areDeliveryDetailsMissing(orderDetails: OrderDetails) {
    return (
      orderDetails.location.country.length < 1 ||
      orderDetails.location.city.length < 1 ||
      orderDetails.location.street.length < 1 ||
      orderDetails.location.buildingNumber.length < 1 ||
      orderDetails.location.postalCode.length < 1
    );
  }

  function isPostalCodeValid(postalCode: string) {
    let postalCodeRegex = /^[0-9]{2}-[0-9]{3}$/;

    return postalCodeRegex.test(postalCode);
  }

  return (
    <>
      <Heading
        color="primary.400"
        textStyle="h2"
        fontSize={headingSize}
        alignSelf="center"
      >
        Cart Summary
      </Heading>
      <VStack border="1px solid black" borderRadius={0}>
        {cartItems.map((item, index) => (
          <Fragment key={index}>
            <CartEntry data={item} baseSize={baseSize} includeIcons={false} />
            <Divider color="black" />
          </Fragment>
        ))}
        <VStack w={baseSize + "px"} spacing={0}>
          <HStack
            w="inherit"
            justifyContent="space-between"
            px="16px"
            textStyle="p"
            fontSize="18px"
          >
            <Text fontWeight={600}>Products Value</Text>
            <Text fontWeight={600}>
              {currency}
              {totalPrice}
            </Text>
          </HStack>
          <HStack
            w="inherit"
            justifyContent="space-between"
            px="16px"
            textStyle="p"
            fontSize="18px"
            color="green.500"
          >
            <Text fontWeight={600}>Delivery Cost</Text>
            <Text fontWeight={600}>
              {currency}
              {orderData.deliveryMethod.price}
            </Text>
          </HStack>
          <HStack
            w="inherit"
            justifyContent="space-between"
            px="16px"
            textStyle="p"
            fontSize="18px"
            color="green.500"
          >
            <Text fontWeight={600}>Payment Cost</Text>
            <Text fontWeight={600}>
              {currency}
              {orderData.paymentMethod.cost}
            </Text>
          </HStack>
        </VStack>
        <Divider color="black" />
        <VStack w={baseSize + "px"} spacing={0}>
          <HStack
            w="inherit"
            justifyContent="space-between"
            px="16px"
            textStyle="p"
            fontSize="18px"
          >
            <Text fontWeight={600}>Total Cost</Text>
            <Text fontWeight={600}>
              {currency}
              {finalPrice}
            </Text>
          </HStack>
          <HStack
            w="inherit"
            justifyContent="space-between"
            px="16px"
            textStyle="p"
            fontSize="18px"
            color="green.500"
          >
            <Text fontWeight={600}>Including Discount</Text>
            <Text fontWeight={600}>
              {currency}
              {totalDiscount}
            </Text>
          </HStack>
        </VStack>
      </VStack>
      <Button
        w={baseSize + "px"}
        bg="primary.300"
        color="white"
        borderRadius={0}
        _hover={{ bg: "primary.400" }}
        _active={{ bg: "primary.400" }}
        onClick={placeOrder}
      >
        Place Order
      </Button>
      <ConfirmOrderModal
        isOpen={isModalOpen}
        setIsOpen={setIsModalOpen}
        changeCurrentCartStep={changeCurrentCartStep}
        orderData={orderData}
        orderDetails={orderDetails}
      />
    </>
  );
};

export default DiscountCartSummary;