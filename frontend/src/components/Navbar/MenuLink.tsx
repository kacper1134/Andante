import { Box, Text, VStack, Menu, MenuButton, MenuList, MenuItem } from "@chakra-ui/react";
import { useToken } from "@chakra-ui/react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import path from "path";

export interface Link {
  text: string;
  path?: string;
  submenu?: Link[];
}

export interface MenuLinkProps {
  link: Link & { to?: string };
}

const MenuLink: React.FC<MenuLinkProps> = ({ link }) => {
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
  const [isHovered, setIsHovered] = useState(false);
  const [isDropdownHovered, setIsDropdownHovered] = useState(false);

  const { t } = useTranslation();

  const handleLinkClick = (path?: string) => {
    setIsDropdownHovered(false);
    if (path) {
      navigate(path);
    }
  };

  useEffect(() => {
    setIsHovered(false);
    setIsDropdownHovered(false);
  }, [location.pathname]);

  return (
    <Box
      h="inherit"
      lineHeight="inherit"
      position="relative"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <Text
        as="span"
        textStyle="h3"
        fontSize={linkSize}
        color={isHovered ? "primary.700" : "primary.300"}
        cursor={link.path ? "pointer" : "default"}
        userSelect="none"
        onClick={link.path ? () => navigate(link.path || "") : () => {}}
      >
        {t(link.text).toUpperCase()}
      </Text>
      {(isHovered || isDropdownHovered) && link.submenu && (
        <Box
          position="absolute"
          top="80%"
          left="0"
          zIndex="1"
          boxShadow="md"
          background="white"
          borderRadius="md"
          onMouseEnter={() => setIsDropdownHovered(true)}
          onMouseLeave={() => setIsDropdownHovered(false)}
        >
          {link.submenu.map((subitem) => (
            <Text
              key={subitem.text}
              onClick={() => handleLinkClick(subitem.path)}
              fontSize={linkSize}
              color={location.pathname.startsWith(subitem.path || "") ? "primary.700" : "primary.300"}
              _hover={{ color: "primary.700" }}
              cursor={subitem.path ? "pointer" : "default"}
              userSelect="none"
              pl="4"
              pr="4"
            >
              {t(subitem.text).toUpperCase()}
            </Text>
          ))}
        </Box>
      )}
    </Box>
  );
};


export default MenuLink;
