import { useDispatch, useSelector } from "react-redux";
import {
  BillDetails,
  ContactData,
  DeliveryDetails,
  DeliveryMethod,
  PaymentMethods,
} from "../../Cart/CartForms";
import { PaymentMethodData } from "../../Cart/CartForms";
import { RootState } from "../../../store";
import { useEffect, useState } from "react";
import {
  VStack,
  Wrap,
  useBreakpointValue,
  Text,
  Button,
  Spacer,
  useToast,
} from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { cartActions } from "../../../store/cart/cartSlice";
import { OrderDetails } from "../../Cart/CartConfirmation";

export interface OrderPreferencesFormsProps {}

const deliveryMethods: DeliveryMethod[] = [
  {
    provider: "Courier InPost, UPS, DPD, DTS",
    expectedDeliveryDays: 1,
    price: 6.49,
    type: "Courier",
  },
  {
    provider: "Pickup Point",
    expectedDeliveryDays: 2,
    price: 4.99,
    type: "Pickup Point",
  },
];

const mockPaymentMethods: PaymentMethodData[] = [
  {
    provider: "Online payment(PayU)",
    description: "Quick transfer, card, BLIK",
    cost: 0,
  },
  {
    provider: "PayPo - buy now, pay in 30 days",
    description:
      "Buy now, check product and pay in up to 30 days without interest",
    cost: 0,
  },
  {
    provider: "Pay on delivery",
    description: "Pay after receiving the order",
    cost: 3,
  },
];

const OrderPreferencesForms: React.FC<OrderPreferencesFormsProps> = () => {
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const deliveryPickupLocation = useSelector(
    (state: RootState) => state.cart.deliveryPickupLocation
  );

  const orderPreferences = useSelector(
    (state: RootState) => state.cart.orderPreferences
  );

  const [personalDetails, setPersonalDetails] = useState({
    name:
      orderPreferences.orderDetails.personal.name !== ""
        ? orderPreferences.orderDetails.personal.name
        : userDetails?.personal.name ?? "",
    surname:
      orderPreferences.orderDetails.personal.surname !== ""
        ? orderPreferences.orderDetails.personal.surname
        : userDetails?.personal.surname ?? "",
    emailAddress:
      orderPreferences.orderDetails.personal.emailAddress !== ""
        ? orderPreferences.orderDetails.personal.emailAddress
        : userDetails?.personal.emailAddress ?? "",
    phoneNumber:
      orderPreferences.orderDetails.personal.phoneNumber !== ""
        ? orderPreferences.orderDetails.personal.phoneNumber
        : userDetails?.personal.phoneNumber ?? "",
  });
  const [deliveryDetails, setDeliveryDetails] = useState({
    country:
      orderPreferences.orderDetails.location.country !== ""
        ? orderPreferences.orderDetails.location.country
        : userDetails?.delivery.country ?? "",
    city:
      orderPreferences.orderDetails.location.city !== ""
        ? orderPreferences.orderDetails.location.city
        : userDetails?.delivery.city ?? "",
    street:
      orderPreferences.orderDetails.location.street !== ""
        ? orderPreferences.orderDetails.location.street
        : userDetails?.delivery.street ?? "",
    buildingNumber: orderPreferences.orderDetails.location.buildingNumber ?? "",
    flatNumber: orderPreferences.orderDetails.location.flatNumber ?? "",
    postalCode:
      orderPreferences.orderDetails.location.postalCode !== ""
        ? orderPreferences.orderDetails.location.postalCode
        : userDetails?.delivery.postalCode ?? "",
  });

  const [paymentMethod, setPaymentMethod] = useState(
    orderPreferences.paymentMethod.description !== ""
      ? orderPreferences.paymentMethod
      : mockPaymentMethods[0]
  );

  const [deliveryMethod, setDeliveryMethod] = useState(
    orderPreferences.deliveryMethod.provider !== ""
      ? orderPreferences.deliveryMethod
      : deliveryMethods[0]
  );

  const headingSize = useBreakpointValue({
    base: "20px",
    md: "24px",
    lg: "28px",
    xl: "32px",
  })!;

  const { initialized } = useKeycloak();
  const toast = useToast();

  useEffect(() => {
    if (initialized) {
      setDeliveryDetails({
        country:
          orderPreferences.orderDetails.location.country !== ""
            ? orderPreferences.orderDetails.location.country
            : userDetails?.delivery.country ?? "",
        city:
          orderPreferences.orderDetails.location.city !== ""
            ? orderPreferences.orderDetails.location.city
            : userDetails?.delivery.city ?? "",
        street:
          orderPreferences.orderDetails.location.street !== ""
            ? orderPreferences.orderDetails.location.street
            : userDetails?.delivery.street ?? "",
        buildingNumber:
          orderPreferences.orderDetails.location.buildingNumber ?? "",
        flatNumber: orderPreferences.orderDetails.location.flatNumber ?? "",
        postalCode:
          orderPreferences.orderDetails.location.postalCode !== ""
            ? orderPreferences.orderDetails.location.postalCode
            : userDetails?.delivery.postalCode ?? "",
      });
      setPersonalDetails({
        name:
          orderPreferences.orderDetails.personal.name !== ""
            ? orderPreferences.orderDetails.personal.name
            : userDetails?.personal.name ?? "",
        surname:
          orderPreferences.orderDetails.personal.surname !== ""
            ? orderPreferences.orderDetails.personal.surname
            : userDetails?.personal.surname ?? "",
        emailAddress:
          orderPreferences.orderDetails.personal.emailAddress !== ""
            ? orderPreferences.orderDetails.personal.emailAddress
            : userDetails?.personal.emailAddress ?? "",
        phoneNumber:
          orderPreferences.orderDetails.personal.phoneNumber !== ""
            ? orderPreferences.orderDetails.personal.phoneNumber
            : userDetails?.personal.phoneNumber ?? "",
      });
    }
  }, [
    initialized,
    orderPreferences.orderDetails.location.buildingNumber,
    orderPreferences.orderDetails.location.city,
    orderPreferences.orderDetails.location.country,
    orderPreferences.orderDetails.location.flatNumber,
    orderPreferences.orderDetails.location.postalCode,
    orderPreferences.orderDetails.location.street,
    orderPreferences.orderDetails.personal.emailAddress,
    orderPreferences.orderDetails.personal.name,
    orderPreferences.orderDetails.personal.phoneNumber,
    orderPreferences.orderDetails.personal.surname,
    userDetails?.delivery,
    userDetails?.personal,
  ]);

  const headerSize = useBreakpointValue({
    base: 24,
    md: 32,
    xl: 36,
  })!;

  const textSize = useBreakpointValue({
    base: 14,
    md: 20,
    xl: 24,
  })!;

  const dispatch = useDispatch();

  const updateOrderPreferences = () => {
    if (
      arePersonalDetailsMissing({
        personal: personalDetails,
        location: deliveryDetails,
      })
    ) {
      toast({
        title: "Missing Personal Details",
        description:
          "Required personal details are missing. Please fill them in before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else if (
      areDeliveryDetailsMissing({
        personal: personalDetails,
        location: deliveryDetails,
      })
    ) {
      toast({
        title: "Missing Delivery Details",
        description:
          "Required delivery details are missing. Please fill them in before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else if (
      deliveryMethod.type === "Pickup Point" &&
      !deliveryPickupLocation
    ) {
      toast({
        title: "Missing Pickup Location",
        description:
          "Required pickup point location is missing. Please fill it in before proceeding",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else if (!isPostalCodeValid(deliveryDetails.postalCode)) {
      toast({
        title: "Invalid Postal Code",
        description:
          "Provided postal code does not match required pattern of two digits followed by hyphen and three digits",
        status: "error",
        duration: 5000,
        isClosable: true,
      });
    } else {
      dispatch(cartActions.updatePreferedDeliveryMethod(deliveryMethod));
      dispatch(cartActions.updatePreferedPaymentMethod(paymentMethod));
      dispatch(
        cartActions.updatePreferedOrderDetails({
          personal: personalDetails,
          location: deliveryDetails,
        })
      );

      dispatch(cartActions.updateDeliveryMethod(deliveryMethod));
      dispatch(cartActions.updatePaymentMethod(paymentMethod));
      dispatch(
        cartActions.updateOrderDetails({
          personal: personalDetails,
          location: deliveryDetails,
        })
      );

      toast({
        title: "Updated Order Preferences",
        description: "Your order preferences were successfully updated!",
        status: "success",
        isClosable: true,
        duration: 4000,
      });
    }
  };

  return (
    <>
      <Text
        textStyle="h3"
        color="primary.300"
        fontSize={headerSize + "px"}
        textAlign="center"
        w="100%"
        bg="purple.50"
        boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
      >
        Order Preferences
      </Text>
      <VStack
        h="90%"
        w="99%"
        ml="0.5%"
        my="0.5%"
        boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
        pb="10px"
      >
        <Wrap spacing="10px" p="20px">
          <ContactData
            headingSize={headingSize}
            personalDetails={personalDetails}
            setPersonalDetails={setPersonalDetails}
          />
          <BillDetails
            headingSize={headingSize}
            deliveryDetails={deliveryDetails}
            setDeliveryDetails={setDeliveryDetails}
          />
          <DeliveryDetails
            headingSize={headingSize}
            deliveryMethods={deliveryMethods}
            deliveryMethod={deliveryMethod}
            setDeliveryMethod={setDeliveryMethod}
          />
          <PaymentMethods
            headerSize={headingSize}
            paymentMethods={mockPaymentMethods}
            paymentMethod={paymentMethod}
            setPaymentMethod={setPaymentMethod}
          />
          <Spacer />
          <VStack>
            <Spacer />
            <Button
              colorScheme="primary"
              textStyle="p"
              fontSize={textSize}
              h="fit-content"
              py="10px"
              mr="5px"
              onClick={updateOrderPreferences}
            >
              Update
            </Button>
          </VStack>
        </Wrap>
      </VStack>
    </>
  );
};

export const arePersonalDetailsMissing = (orderDetails: OrderDetails) => {
  return (
    orderDetails.personal.name.length < 1 ||
    orderDetails.personal.surname.length < 1 ||
    orderDetails.personal.emailAddress.length < 1 ||
    orderDetails.personal.phoneNumber.length !== 9
  );
};

export const areDeliveryDetailsMissing = (orderDetails: OrderDetails) => {
  return (
    orderDetails.location.country.length < 1 ||
    orderDetails.location.city.length < 1 ||
    orderDetails.location.street.length < 1 ||
    orderDetails.location.buildingNumber.length < 1 ||
    orderDetails.location.postalCode.length < 1
  );
};

export const isPostalCodeValid = (postalCode: string) => {
  let postalCodeRegex = /^[0-9]{2}-[0-9]{3}$/;

  return postalCodeRegex.test(postalCode);
};

export default OrderPreferencesForms;
