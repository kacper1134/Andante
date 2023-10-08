import { Box } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import { useEffect, useState } from "react";
import { useStartPaymentMutation } from "../../../store/api/order-api-slice";
import CheckoutForm from "./CheckoutForm";
import { appearance } from "./PaymentApperance";

const stripePromise = loadStripe(process.env.REACT_APP_PUBLIC_KEY as string);

type PaymentWrapperProps = {
  orderId: number;
};

const PaymentWrapper = ({ orderId }: PaymentWrapperProps) => {
  const [clientSecret, setClientSecret] = useState("");
  const [startPayment] = useStartPaymentMutation();
  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    const getClientSecret = async () => {
      try {
        const secret = await startPayment(orderId).unwrap();
        setClientSecret(secret);
      } catch (error) {
        console.log(error);
      }
    };

    if (initialized && keycloak.authenticated) {
      getClientSecret();
    }
  }, [initialized, keycloak.authenticated, orderId, startPayment]);

  const options = {
    clientSecret,
    appearance,
  };

  return (
    <Box w="100%" h="100%" bg="primary.50">
      {clientSecret && (
        <Elements options={options} stripe={stripePromise}>
          <CheckoutForm />
        </Elements>
      )}
    </Box>
  );
};

export default PaymentWrapper;
