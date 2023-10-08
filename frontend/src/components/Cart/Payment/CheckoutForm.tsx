import {
  Button,
  Center,
  FormControl,
  Spinner,
  Text,
  useToast,
} from "@chakra-ui/react";
import {
  PaymentElement,
  useElements,
  useStripe,
} from "@stripe/react-stripe-js";
import { useState } from "react";
import CancelModal from "./CancelModal";

const CheckoutForm = () => {
  const stripe = useStripe();
  const elements = useElements();

  const toast = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const [isOpen, setIsOpen] = useState(false);

  const handleSubmit = async (e: React.SyntheticEvent) => {
    e.preventDefault();

    if (!stripe || !elements) return;

    setIsLoading(true);

    const { error } = await stripe?.confirmPayment({
      elements,
      confirmParams: {
        return_url:
          document.location.protocol + "//" + document.location.host + "/shop",
      },
    });

    if (error.type === "card_error" || error.type === "validation_error") {
      toast({
        title: "Error",
        description: error!.message,
        status: "error",
        isClosable: true,
      });
    } else {
      toast({
        title: "Error",
        description: "An unexpected error occured.",
        status: "error",
        isClosable: true,
        duration: 2000,
      });
    }

    setIsLoading(false);
  };

  return (
    <Center mt="30px">
      <FormControl w="95%">
        <PaymentElement />
        <Button
          mt="20px"
          px="50px"
          disabled={isLoading || !stripe || !elements}
          colorScheme="primary"
          onClick={handleSubmit}
          textStyle="p"
        >
          {isLoading ? <Spinner /> : <Text>Pay now</Text>}
        </Button>
        <Button
          mt="20px"
          ml="20px"
          px="50px"
          disabled={isLoading || !stripe || !elements}
          bg="purple.300"
          _hover={{bg: "purple.400"}}
          _active={{bg: "purple.400"}}
          color="white"
          onClick={() => setIsOpen(true)}
          textStyle="p"
        >
          {isLoading ? <Spinner /> : <Text>Cancel</Text>}
        </Button>
      </FormControl>
      <CancelModal isOpen={isOpen} setIsOpen={setIsOpen} />
    </Center>
  );
};

export default CheckoutForm;
