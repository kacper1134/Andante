import { HStack, Text, Icon } from "@chakra-ui/react";
import { useTranslation } from "react-i18next";
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
  const {t} = useTranslation();
  return (
    <HStack>
      <Icon
        as={FaRegCopyright}
        color="primary.500"
        boxSize={copyrightIconSize}
      />
      <Text color="primary.500" fontWeight="semibold" fontSize={footerFontSize} textStyle="p">
        {t("copyright")}
      </Text>
    </HStack>
  );
};

export default Copyright;
