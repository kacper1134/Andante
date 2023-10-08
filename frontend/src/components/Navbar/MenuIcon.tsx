import { IconButton } from "@chakra-ui/react";
import { JSXElementConstructor, ReactElement } from "react";
import { motion, Variants } from "framer-motion";
import { useToken } from "@chakra-ui/react";

type MenuIconProps = {
  label: string;
  icon: ReactElement<any, string | JSXElementConstructor<any>>;
  iconColor: string;
  hoverColor: string;
};

const MenuIcon = ({ label, icon, iconColor, hoverColor }: MenuIconProps) => {
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

  return (
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
  );
};

export default MenuIcon;
