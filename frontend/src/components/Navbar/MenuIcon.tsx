import { Badge, Box, HStack, IconButton } from "@chakra-ui/react";
import { JSXElementConstructor, ReactElement } from "react";
import { motion, Variants } from "framer-motion";
import { useToken } from "@chakra-ui/react";
import { useSelector } from "react-redux";
import { RootState } from "../../store";

type MenuIconProps = {
  label: string;
  icon: ReactElement<any, string | JSXElementConstructor<any>>;
  iconColor: string;
  hoverColor: string;
  isCart: boolean;
};

const MenuIcon = ({
  label,
  icon,
  iconColor,
  hoverColor,
  isCart,
}: MenuIconProps) => {
  const [initialColor, animationColor] = useToken("colors", [
    iconColor,
    hoverColor,
  ]);

  const menuIconVariants: Variants = {
    initial: {
      color: initialColor,
    },
    hover: {
      color: animationColor,
    },
  };

  const cartItems = useSelector((state: RootState) => state.cart.cartItems);
  const numberOfItemsInCart = cartItems.map((item) => item.quantity).reduce((a, b) => a + b, 0);

  return (
    <HStack position="relative">
      <IconButton
        as={motion.button}
        aria-label={label}
        icon={icon}
        variant="link"
        color={iconColor}
        variants={menuIconVariants}
        initial="initial"
        whileHover="hover"
      />
      {(isCart && numberOfItemsInCart > 0) && (
        <Badge
          position="absolute"
          top={{base: -1, md: -2}}
          right={{base: 1, md: -1}}
          bg="primary.500"
          color="white"
          borderRadius="50%"
          width={{base: "9px", md: "11px", lg: "13px", xl: "15px", "2xl": "17px"}}
          height={{base: "9px", md: "11px", lg: "13px", xl: "15px", "2xl": "17px"}}
          fontSize={{base: "7px", md: "8px", lg: "9px", xl: "10px", "2xl": "12px"}}
          display="flex"
          alignItems="center"
          justifyContent="center"
          padding="3px"
        >
          {numberOfItemsInCart}
        </Badge>
      )}
    </HStack>
  );
};

export default MenuIcon;
