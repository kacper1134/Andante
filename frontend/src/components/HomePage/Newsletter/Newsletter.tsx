import { VStack, Image, HStack, Text } from "@chakra-ui/react";
import NewsletterButton from "./NewsletterButton";

const Newsletter = () => {
  const sizes = {
    base: "250px",
    sm: "300px",
    md: "500px",
    lg: "600px",
    xl: "650px",
  };
  const headerSizes = {
    base: "15px",
    sm: "20px",
    md: "30px",
    lg: "35px",
    xl: "40px",
  };
  return (
    <VStack p={8} position="relative">
      <Image src="/Newsletter.png" boxSize={sizes} />
      <HStack boxSize={sizes} position="absolute" top="0">
        <VStack width="full" spacing={5}>
          <Text
            textAlign="center"
            width="100%"
            color="primary.300"
            fontSize={headerSizes}
            textStyle="h1"
          >
            Become Andante Friend
          </Text>
          <NewsletterButton />
        </VStack>
      </HStack>
    </VStack>
  );
};

export default Newsletter;
