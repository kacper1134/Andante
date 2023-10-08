import { HStack, Icon, Menu, MenuButton, MenuList, MenuItem, IconButton, useBreakpointValue, Box } from "@chakra-ui/react";
import {
  MdOutlinePersonOutline,
  MdShoppingCart,
} from "react-icons/md";
import { AiFillMessage } from "react-icons/ai";
import MenuIcon from "./MenuIcon";
import { ReactElement, JSXElementConstructor } from 'react';
import { HamburgerIcon } from "@chakra-ui/icons";
import { LINKS } from "./MenuLinks";
import { useNavigate } from "react-router-dom";
import { IoIosNotifications } from "react-icons/io";

export interface IconDescription {
  label: string,
  icon: ReactElement<any, string | JSXElementConstructor<any>>
};

const iconColor = "purple.500";

const hoverColor = "purple.700";

const MenuIcons: React.FC<{ isDropdown: boolean }> = ({ isDropdown }) => {
  const navigate = useNavigate();

  const iconSize: string = useBreakpointValue({
    base: "12px",
    sm: "16px",
    md: "20px",
    lg: "24px",
    xl: "28px",
    '2xl': "32px",
  })!;

  const profileHandler = () => {
    navigate("/profile");
  }

  const cartHandler = () => {
    navigate("/cart");
  }

  const notificationsHandler = () => {
    navigate("/notifications");
  }

  const icons: IconDescription[] = [
    {
      label: "Check your notifications",
      icon: <Icon as={IoIosNotifications} boxSize={iconSize} onClick={notificationsHandler} />
    },
    {
      label: "Go to user settings",
      icon: <Icon as={MdOutlinePersonOutline} boxSize={iconSize} onClick={profileHandler} />
    },
    {
      label: "Go to your cart",
      icon: <Icon as={MdShoppingCart} boxSize={iconSize} onClick={cartHandler} />
    },
    {
      label: "Check user messages",
      icon: <Icon as={AiFillMessage} boxSize={iconSize} onClick={() => navigate("/chat")} />
    }
  ];


  return (
    <HStack>
      {icons.map(icon => <MenuIcon key={icon.label} label={icon.label} icon={icon.icon} iconColor={iconColor} hoverColor={hoverColor} />)}
      {isDropdown && <DropdownMenu iconSize={iconSize} />}
    </HStack>
  );
};

const DropdownMenu: React.FC<{ iconSize: string }> = ({ iconSize }) => {
  const navigate = useNavigate();

  const capitalize = (word: string) => {
    return word.charAt(0).toUpperCase() + word.slice(1);
  }

  return <Box>
    <Menu>
      <MenuButton
        backgroundColor="inherit"
        _hover={{}}
        as={IconButton}
        icon={<DropdownIcon iconSize={iconSize} />}
      />
      <MenuList zIndex="dropdown">
        {LINKS.map(link => <MenuItem key={link.text} onClick={() => navigate(link.path)} color="purple.300">{capitalize(link.text)}</MenuItem>)}
      </MenuList>
    </Menu>
  </Box>
}

const DropdownIcon: React.FC<{ iconSize: string }> = ({ iconSize }) => {
  return <Icon as={HamburgerIcon} boxSize={iconSize} color={iconColor} _hover={{ color: hoverColor }} />;
}

export default MenuIcons;
