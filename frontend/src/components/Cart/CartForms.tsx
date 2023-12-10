import {
  ChangeEvent,
  Dispatch,
  SetStateAction,
  useEffect,
  useState,
} from "react";
import {
  VStack,
  Wrap,
  Heading,
  useBreakpointValue,
  Input,
  Text,
  Radio,
  RadioGroup,
  HStack,
  Spacer,
  Button,
} from "@chakra-ui/react";
import { useSelector } from "react-redux";
import { RootState } from "../../store";
import { DateTime } from "luxon";
import DiscountCartSummary from "./DiscountCartSummary";
import DeliveryMethodModal from "./Delivery/DeliveryMethodModal";
import PickupLocationEdit from "./Delivery/PickupLocationEdit";
import { useKeycloak } from "@react-keycloak/web";
import { userDetailsWidth } from "./CartSizes";
import { useTranslation } from "react-i18next";

export interface CartFormsProps {
  changeCurrentCartStep: Dispatch<SetStateAction<number>>;
}

const deliveryMethods: DeliveryMethod[] = [
  {
    provider: "profilePage.preferences.delivery.courier.label",
    expectedDeliveryDays: 1,
    price: 6.49,
    type: "Courier",
  },
  {
    provider: "profilePage.preferences.delivery.pickup.label",
    expectedDeliveryDays: 2,
    price: 4.99,
    type: "Pickup Point",
  },
];

const mockPaymentMethods: PaymentMethodData[] = [
  {
    provider: "Online payment(PayU)",
    description: "profilePage.preferences.payment.online.content",
    cost: 0,
  },
  {
    provider: "PayPo - buy now, pay in 30 days",
    description: "profilePage.preferences.payment.payPo.content",
    cost: 0,
  },
  {
    provider: "Pay on delivery",
    description: "profilePage.preferences.payment.delivery.content",
    cost: 3,
  },
];

const CartForms: React.FC<CartFormsProps> = ({ changeCurrentCartStep }) => {
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
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

  const baseSize = useBreakpointValue({
    base: 350,
    md: 600,
    lg: 800,
    xl: 1000,
    "2xl": 1200,
  })!;

  return (
    <VStack>
      <Wrap spacing="24px" maxW={baseSize}>
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
      </Wrap>
      <DiscountCartSummary
        headingSize={headingSize}
        changeCurrentCartStep={changeCurrentCartStep}
        orderData={{
          orderDetails: {
            personal: personalDetails,
            location: deliveryDetails,
          },
          paymentMethod: paymentMethod,
          deliveryMethod: deliveryMethod,
        }}
      />
    </VStack>
  );
};

interface ContactDataProps {
  headingSize: string;
  personalDetails: {
    name: string;
    surname: string;
    emailAddress: string;
    phoneNumber: string;
  };
  setPersonalDetails: Dispatch<
    SetStateAction<{
      name: string;
      surname: string;
      emailAddress: string;
      phoneNumber: string;
    }>
  >;
}

export const ContactData: React.FC<ContactDataProps> = ({
  headingSize,
  personalDetails,
  setPersonalDetails,
}) => {
  function handleNameInput(e: ChangeEvent<HTMLInputElement>) {
    setPersonalDetails((prevState) => ({ ...prevState, name: e.target.value }));
  }

  function handleSurnameInput(e: ChangeEvent<HTMLInputElement>) {
    setPersonalDetails((prevState) => ({
      ...prevState,
      surname: e.target.value,
    }));
  }

  function handleEmailInput(e: ChangeEvent<HTMLInputElement>) {
    setPersonalDetails((prevState) => ({
      ...prevState,
      emailAddress: e.target.value,
    }));
  }

  function handlePhoneInput(e: ChangeEvent<HTMLInputElement>) {
    setPersonalDetails((prevState) => ({
      ...prevState,
      phoneNumber: e.target.value,
    }));
  }
  const {t} = useTranslation();
  return (
    <VStack w={userDetailsWidth} alignSelf="start">
      <Heading
        textStyle="h3"
        alignSelf="start"
        fontSize={headingSize}
        color="primary.400"
      >
        {t("profilePage.preferences.contact.title")}
      </Heading>
      <VStack border="1px solid black" w="inherit" p="16px">
        <CartInput
          placeholder={t("profilePage.preferences.contact.name")}
          value={personalDetails.name}
          onChange={handleNameInput}
          isRequired
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.contact.surname")}
          value={personalDetails.surname}
          onChange={handleSurnameInput}
          isRequired
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.contact.email")}
          value={personalDetails.emailAddress}
          onChange={handleEmailInput}
          isRequired
          disabled
        />
        <CartInput
          placeholder={t("profilePage.preferences.contact.phone")}
          value={personalDetails.phoneNumber}
          onChange={handlePhoneInput}
          isRequired
          disabled={false}
        />
        <Text
          textStyle="p"
          fontSize="12px"
          alignSelf="start"
          color="primary.400"
        >
          {t("profilePage.preferences.subText")}
        </Text>
      </VStack>
    </VStack>
  );
};

interface BillDetailsProps {
  headingSize: string;
  deliveryDetails: {
    country: string;
    city: string;
    street: string;
    buildingNumber: string;
    flatNumber: string;
    postalCode: string;
  };
  setDeliveryDetails: Dispatch<
    SetStateAction<{
      country: string;
      city: string;
      street: string;
      buildingNumber: string;
      flatNumber: string;
      postalCode: string;
    }>
  >;
}

export const BillDetails: React.FC<BillDetailsProps> = ({
  headingSize,
  deliveryDetails,
  setDeliveryDetails,
}) => {
  function handleCountryInput(e: ChangeEvent<HTMLInputElement>) {
    setDeliveryDetails((prevState) => ({
      ...prevState,
      country: e.target.value,
    }));
  }

  function handleCityInput(e: ChangeEvent<HTMLInputElement>) {
    setDeliveryDetails((prevState) => ({ ...prevState, city: e.target.value }));
  }

  function handleStreetInput(e: ChangeEvent<HTMLInputElement>) {
    setDeliveryDetails((prevState) => ({
      ...prevState,
      street: e.target.value,
    }));
  }

  function handleBuildingNumber(e: ChangeEvent<HTMLInputElement>) {
    setDeliveryDetails((prevState) => ({
      ...prevState,
      buildingNumber: e.target.value,
    }));
  }

  function handleFlatInput(e: ChangeEvent<HTMLInputElement>) {
    setDeliveryDetails((prevState) => ({
      ...prevState,
      flatNumber: e.target.value,
    }));
  }

  function handlePostalCode(e: ChangeEvent<HTMLInputElement>) {
    setDeliveryDetails((prevState) => ({
      ...prevState,
      postalCode: e.target.value,
    }));
  }
  const {t} = useTranslation();
  return (
    <VStack w={userDetailsWidth} alignSelf="start">
      <Heading
        textStyle="h3"
        alignSelf="start"
        fontSize={headingSize}
        color="primary.400"
      >
        {t("profilePage.preferences.billing.title")}
      </Heading>
      <VStack border="1px solid black" w="inherit" p="16px">
        <CartInput
          placeholder={t("profilePage.preferences.billing.country")}
          value={deliveryDetails.country}
          onChange={handleCountryInput}
          isRequired
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.billing.city")}
          value={deliveryDetails.city}
          onChange={handleCityInput}
          isRequired
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.billing.street")}
          value={deliveryDetails.street}
          onChange={handleStreetInput}
          isRequired
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.billing.building")}
          value={deliveryDetails.buildingNumber}
          onChange={handleBuildingNumber}
          isRequired
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.billing.flat")}
          value={deliveryDetails.flatNumber}
          onChange={handleFlatInput}
          isRequired={false}
          disabled={false}
        />
        <CartInput
          placeholder={t("profilePage.preferences.billing.postalcode")}
          value={deliveryDetails.postalCode}
          onChange={handlePostalCode}
          isRequired
          disabled={false}
        />
        <Text
          textStyle="p"
          fontSize="12px"
          alignSelf="start"
          color="primary.400"
        >
          {t("profilePage.preferences.subText")}
        </Text>
      </VStack>
    </VStack>
  );
};

export interface DeliveryMethod {
  provider: string;
  expectedDeliveryDays: number;
  price: number;
  type: "Courier" | "Pickup Point";
}

interface DeliveryDetailsProps {
  headingSize: string;
  deliveryMethods: DeliveryMethod[];
  deliveryMethod: DeliveryMethod;
  setDeliveryMethod: Dispatch<SetStateAction<DeliveryMethod>>;
}

export const DeliveryDetails: React.FC<DeliveryDetailsProps> = ({
  headingSize,
  deliveryMethods,
  deliveryMethod,
  setDeliveryMethod,
}) => {
  function handleDeliveryMethod(provider: string) {
    const selectedMethod = deliveryMethods.find(
      (method) => method.provider === provider
    );

    if (selectedMethod) {
      setDeliveryMethod(selectedMethod);
    }
  }
  const {t} = useTranslation();
  return (
    <VStack w={userDetailsWidth} alignSelf="start">
      <Heading
        textStyle="h3"
        alignSelf="start"
        fontSize={headingSize}
        color="primary.400"
      >
        {t("profilePage.preferences.delivery.title")}
      </Heading>
      <RadioGroup
        onChange={handleDeliveryMethod}
        value={deliveryMethod.provider}
        alignSelf="start"
        border="1px solid black"
        w="inherit"
        p="16px"
      >
        <VStack spacing="4px">
          {deliveryMethods.map((method, index) => (
            <DeliveryOption
              key={index}
              method={method}
              selected={deliveryMethod === method}
            />
          ))}
        </VStack>
      </RadioGroup>
    </VStack>
  );
};

interface DeliveryOptionProps {
  method: DeliveryMethod;
  selected: boolean;
}

export interface DeliveryPickupLocation {
  country: string;
  city: string;
  street: string;
  streetNumber: string;
  postCode: string;
  brand: string;
  code: string;
  operator: string;
}

const DeliveryOption: React.FC<DeliveryOptionProps> = ({
  method,
  selected,
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const deliveryLocation = useSelector(
    (state: RootState) => state.cart.deliveryPickupLocation
  );
  const {t} = useTranslation(); 
  const pickUpButton = (
    <Button
      alignSelf="flex-start"
      size="sm"
      colorScheme="primary"
      onClick={() => setIsModalOpen(true)}
      textStyle="p"
    >
      {t("profilePage.preferences.delivery.pickup.buttonText")}
    </Button>
  ); 
  return (
    <>
      <DeliveryMethodModal isOpen={isModalOpen} setIsOpen={setIsModalOpen} />
      <Radio
        colorScheme="primary"
        value={method.provider}
        alignSelf="start"
        w="400px"
        mb="10px"
      >
        <HStack w="inherit">
          <VStack w="inherit">
            <HStack spacing="4px" alignSelf="start">
              <Text textStyle="p">{t(method.provider)}</Text>
            </HStack>
            <Spacer />
            {method.type === "Pickup Point" &&
              selected &&
              !deliveryLocation &&
              pickUpButton}
            {method.type === "Pickup Point" && selected && deliveryLocation && (
              <PickupLocationEdit setIsModalOpen={setIsModalOpen} />
            )}
            <Text textStyle="p" fontSize="12px">
              {t("profilePage.preferences.delivery.courier.content")}{" "}
              {DateTime.now()
                .plus({ days: method.expectedDeliveryDays })
                .toLocaleString(DateTime.DATE_FULL)}
            </Text>
          </VStack>
        </HStack>
      </Radio>
    </>
  );
};

export interface PaymentMethodData {
  provider: string;
  description: string;
  cost: number;
}

interface PaymentMethodsProps {
  headerSize: string;
  paymentMethods: PaymentMethodData[];
  paymentMethod: PaymentMethodData;
  setPaymentMethod: Dispatch<SetStateAction<PaymentMethodData>>;
}

export const PaymentMethods: React.FC<PaymentMethodsProps> = ({
  headerSize,
  paymentMethods,
  paymentMethod,
  setPaymentMethod,
}) => {
  function handlePaymentMethod(provider: string) {
    const selectedMethod = paymentMethods.find(
      (method) => method.provider === provider
    );

    if (selectedMethod) {
      setPaymentMethod(selectedMethod);
    }
  }
  const { t } = useTranslation();
  return (
    <VStack w={userDetailsWidth} alignSelf="start">
      <Heading
        textStyle="h3"
        alignSelf="start"
        fontSize={headerSize}
        color="primary.400"
      >
        {t("profilePage.preferences.payment.title")}
      </Heading>
      <RadioGroup
        onChange={handlePaymentMethod}
        value={paymentMethod.provider}
        alignSelf="start"
        border="1px solid black"
        w="inherit"
        p="16px"
      >
        <VStack spacing="4px" w="inherit">
          {paymentMethods.map((payment, index) => (
            <PaymentMethod key={index} method={payment} />
          ))}
        </VStack>
      </RadioGroup>
    </VStack>
  );
};

interface PaymentMethodProps {
  method: PaymentMethodData;
}

const PaymentMethod: React.FC<PaymentMethodProps> = ({ method }) => {
  const { t } = useTranslation();
  return (
    <Radio
      colorScheme="primary"
      value={method.provider}
      alignSelf="start"
      w="400px"
    >
      <HStack w="400px">
        <VStack spacing={0} alignSelf="start" maxW="300px">
          <Text
            textStyle="p"
            fontWeight={600}
            fontSize="14px"
            alignSelf="start"
          >
            {t(method.provider)}
          </Text>
          <Text
            color="gray.500"
            fontSize="12px"
            noOfLines={3}
            textStyle="p"
            alignSelf="start"
          >
            {t(method.description)}
          </Text>
        </VStack>
        <Spacer />
      </HStack>
    </Radio>
  );
};

interface CartInputProps {
  placeholder: string;
  value: string;
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
  isRequired: boolean;
  disabled: boolean;
}

const CartInput: React.FC<CartInputProps> = ({
  placeholder,
  value,
  onChange,
  isRequired,
  disabled,
}) => {
  return (
    <Input
      textStyle="p"
      placeholder={placeholder}
      _placeholder={{ color: "black" }}
      variant="flushed"
      value={value}
      onChange={onChange}
      _focusVisible={{ borderColor: "primary.400" }}
      required={isRequired}
      disabled={disabled}
    />
  );
};

export default CartForms;
