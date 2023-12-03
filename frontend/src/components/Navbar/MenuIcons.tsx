import { HStack, Icon, Menu, MenuButton, MenuList, MenuItem, IconButton, useBreakpointValue, Box } from "@chakra-ui/react";
import {
  MdOutlinePersonOutline,
  MdShoppingCart,
} from "react-icons/md";
import MenuIcon from "./MenuIcon";
import { ReactElement, JSXElementConstructor } from 'react';
import { HamburgerIcon } from "@chakra-ui/icons";
import { LINKS } from "./MenuLinks";
import { useNavigate } from "react-router-dom";
import { PL, US } from 'country-flag-icons/react/3x2'
import { RootState } from "../../store";
import { useDispatch, useSelector } from "react-redux";
import { authActions } from "../../store/auth/auth-slice";
import { useTranslation } from "react-i18next";

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

  const language = useSelector((state: RootState) => state.auth.language);
  const { i18n } = useTranslation();
  const dispatch = useDispatch();

  const icons: IconDescription[] = [
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
      icon: <Icon as={language === "pl" ? US : PL} boxSize={iconSize} onClick={() => {
        i18n.changeLanguage(language);
        dispatch(authActions.changeLanguage())
      }} />
    },
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
  const { t } = useTranslation();

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
        {LINKS.map(link => <MenuItem key={link.text} onClick={() => navigate(link.path)} color="purple.300">{capitalize(t(link.text))}</MenuItem>)}
      </MenuList>
    </Menu>
  </Box>
}

const DropdownIcon: React.FC<{ iconSize: string }> = ({ iconSize }) => {
  return <Icon as={HamburgerIcon} boxSize={iconSize} color={iconColor} _hover={{ color: hoverColor }} />;
}

export default MenuIcons;
