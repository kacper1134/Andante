import { HStack, Spacer } from "@chakra-ui/react";
import Copyright from "./Copyright";
import SocialMediaIcons from "./SocialMediaIcons";

const Footer = () => {
  const footerHeight = {
    base: "32px",
    sm: "40px",
    md: "56px",
    lg: "64px",
    xl: "72px",
  };
  return (
    <HStack
      w="100%"
      px="12px"
      h={footerHeight}
      lineHeight={footerHeight}
      bg="purple.100"
    >
      <Copyright />
      <Spacer />
      <SocialMediaIcons />
    </HStack>
  );
};

export default Footer;
