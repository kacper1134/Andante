import { Button, Input, HStack, useToast } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { useSubscribeToNewsletterMutation } from "../../../store/api/productSlice";

const NewsletterButton = () => {
  const [subscribeToNewsletter, {isSuccess, isError, isLoading} ] = useSubscribeToNewsletterMutation();
  const [email, setEmail] = useState<string>("");
  const emailRegex = new RegExp(/\S+@\S+\.\S+/);
  const toast = useToast();

  const fontSize = {
    base: "12px",
    sm: "14px",
    md: "16px",
    lg: "18px",
    xl: "20px",
  };
  const inputHeight = {
    base: "30px",
    sm: "32px",
    md: "34px",
    lg: "36px",
    xl: "38px",
  };

  const handleSubscribeAction = () => {

    if (email && emailRegex.test(email)) {
      subscribeToNewsletter(email).then(() => setEmail(""));
    } else {
      toast({
        title: "Provided input is invalid",
        description: `Value ${email} is not a valid email address`,
        status: "error",
        duration: 9000,
        isClosable: true,
      })
    }
  };

  useEffect(() => {
    if (isSuccess) {
      toast({
        title: "Successfully subscribed to newsletter",
        description: "Check provided email address for more details",
        status: "success",
        duration: 9000,
        isClosable: true,
      })
    }
  }, [isSuccess, toast]);

  useEffect(() => {
    if (isError) {
      toast({
        title: "Something went wrong",
        description: "Could not subscribe provided given email address to newsletter",
        status: "error",
        duration: 9000,
        isClosable: true,
      })
    }
  }, [isError, toast]);

  return (
    <HStack spacing={-9}>
      <Input
        placeholder="Sign up for newsletter"
        value={email}
        onChange={(event) => setEmail(event.target.value.trim())}
        fontSize={fontSize}
        rounded="3xl"
        height={inputHeight}
        border="2px solid black"
        variant='filled'
        textStyle="p"
      />
      <Button
        fontSize={fontSize}
        height={inputHeight}
        rounded="3xl"
        color="white"
        colorScheme="purple"
        border="1px solid black"
        textStyle="p"
        onClick={handleSubscribeAction}
        disabled={isLoading}
      >
        SEND
      </Button>
    </HStack>
  );
};

export default NewsletterButton;
