import { Box, Text } from "@chakra-ui/react";
import { useToken } from "@chakra-ui/react";
import { useLocation, useNavigate } from "react-router-dom";

export interface MenuLinkProps {
  to: string,
  text: string,
};

const MenuLink = ({ to, text }: MenuLinkProps) => {
  const linkSize = {
    sm: "10px",
    md: "12px",
    lg: "14px",
    xl: "18px",
    '2xl': "22px",
  };
  const navigate = useNavigate();
  const location = useLocation();

  const [basicColor, hoverColor] = useToken("colors", ["primary.300", "primary.700"]);

  return (
    <Box h="inherit" lineHeight="inherit">
      <Text
        onClick={() => navigate(to)}
        textStyle="h3"
        fontSize={linkSize}
        color={location.pathname.startsWith(to) ? hoverColor : basicColor}
        _hover={{ color: hoverColor }}
        cursor="pointer"
        userSelect="none"
      >
        {text.toUpperCase()}
      </Text>
    </Box>
  );
};

export default MenuLink;
