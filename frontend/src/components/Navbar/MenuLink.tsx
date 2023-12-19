import { Box, Text } from "@chakra-ui/react";
import { useToken } from "@chakra-ui/react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

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

  const isBlogPage =
    (location.pathname?.startsWith("/review") ||
      location.pathname?.startsWith("/recommended") ||
      location.pathname?.startsWith("/music")) &&
    link.text === "blog";

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
        color={(location.pathname.startsWith(link.path!) || isBlogPage) ? hoverColor : basicColor}
        cursor={link.path ? "pointer" : "default"}
        userSelect="none"
        onClick={link.path ? () => navigate(link.path || "") : () => { }}
        _hover={{
          color: hoverColor,
        }}
      >
        {t(link.text).toUpperCase()}
        {link.submenu && (
          <Box as="span" ml="0" display="inline-block">
            <Text fontSize={linkSize} color="primary.300" display="inline">
              &#x25BC;
            </Text>
          </Box>
        )}
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
