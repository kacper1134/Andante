import { HStack, Text, Icon } from "@chakra-ui/react";
import { FaRegCopyright } from "react-icons/fa";

const Copyright = () => {
  const footerFontSize = {
    base: "xs",
    sm: "sm",
    md: "md",
    lg: "lg",
    xl: "xl",
  };
  const copyrightIconSize = {
    base: "12px",
    sm: "14px",
    md: "16px",
    lg: "18px",
    xl: "20px",
  };
  return (
    <HStack>
      <Icon
        as={FaRegCopyright}
        color="primary.500"
        boxSize={copyrightIconSize}
      />
      <Text color="primary.500" fontWeight="semibold" fontSize={footerFontSize} textStyle="p">
        Andante 2022 - All Rights Reserved
      </Text>
    </HStack>
  );
};

export default Copyright;
