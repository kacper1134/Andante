import { HStack, Spacer, useBreakpointValue } from "@chakra-ui/react";
import Copyright from "./Copyright";
import SocialMediaIcons from "./SocialMediaIcons";
import AlternativeVersionSwitch from "./AlternativeVersionSwitch";

const Footer = () => {
  const footerHeight = {
    base: "32px",
    sm: "40px",
    md: "56px",
    lg: "64px",
    xl: "72px",
  };

  const showSwitch = useBreakpointValue({
    base: false,
    lg: true,
  })

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
      {showSwitch && <AlternativeVersionSwitch />}
      <SocialMediaIcons />
    </HStack>
  );
};

export default Footer;
