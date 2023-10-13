import { motion, useAnimationControls } from "framer-motion";
import {
  HStack,
  Text,
  Box,
  Icon,
  As,
  useBreakpointValue,
  Button,
} from "@chakra-ui/react";
import { MdOutlineAccountBox } from "react-icons/md";
import { BiHistory } from "react-icons/bi";
import { MdOutlineForum } from "react-icons/md";
import { useLocation, useNavigate } from "react-router-dom";
import {
  profileContentVariants,
  profileLinkVariants,
} from "./ProfileLinkVariants";
import { AiFillEdit } from "react-icons/ai";
import { ImExit } from "react-icons/im";
import { authActions } from "../../../../store/auth/auth-slice";
import { cartActions } from "../../../../store/cart/cartSlice";
import { useDispatch, useSelector } from "react-redux";
import { useKeycloak } from "@react-keycloak/web";
import { RootState } from "../../../../store";
import { useEffect, useState } from "react";

export interface Link {
  icon: As;
  text: string;
  path: string;
}

export interface ProfileLinksProps {
  isSidebarOpened: boolean;
}

export interface ProfileLinkProps {
  link: Link;
  isSelected: boolean;
}

const ProfileLinks: React.FC<ProfileLinksProps> = ({ isSidebarOpened }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { keycloak } = useKeycloak();
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const [communityPath, setCommunityPath] = useState<string>("/profile/community");

  useEffect(() => {
    if (userDetails) {
      setCommunityPath(`/profile/community/${userDetails.personal.username}`);
    }
  }, [userDetails]);

  const links: Link[] = [
    { icon: MdOutlineAccountBox, text: "User Details", path: "/profile/details" },
    { icon: BiHistory, text: "Order History", path: "/profile/orders" },
    { icon: MdOutlineForum, text: "Community", path: communityPath },
  ];

  const logout = () => {
    keycloak.logout({
      redirectUri: "https://" + window.location.host + "/home",
    });
    dispatch(authActions.queueLogout());
    dispatch(cartActions.clearCart());
  };

  return (
    <>
      {isSidebarOpened ? (
        <Box alignSelf="start" pt="16px">
          {links.map((link) => (
            <ProfileLink
              key={link.text}
              link={link}
              isSelected={location.pathname === link.path}
            />
          ))}
        </Box>
      ) : (
        <>
          <HStack spacing="0">
            {links.map((link) => (
              <HorizontalLink
                key={link.text}
                link={link}
                isSelected={location.pathname === link.path}
              />
            ))}
          </HStack>
          <HStack pt="1px" spacing="1px">
            <Button
              leftIcon={<AiFillEdit />}
              onClick={() => navigate("./edit")}
              backgroundColor="primary.400"
              _hover={{backgroundColor: "primary.500"}}
              _active={{backgroundColor: "primary.500"}}
              borderRadius={0}
              color="white"
              cursor="pointer"
              flexGrow={1}
              justifyContent="center"
              borderEnd="1px solid white"
            >
              Edit account
            </Button>
            <Button
              leftIcon={<ImExit />}
              onClick={logout}
              backgroundColor="primary.400"
              _hover={{backgroundColor: "primary.500"}}
              _active={{backgroundColor: "primary.500"}}
              borderRadius={0}
              color="white"
              cursor="pointer"
              flexGrow={1}
              justifyContent="center"
              borderEnd="1px solid white"
            >
              Sign Out
            </Button>
          </HStack>
        </>
      )}
    </>
  );
};

const ProfileLink: React.FC<ProfileLinkProps> = ({ link, isSelected }) => {
  const controls = useAnimationControls();
  const navigate = useNavigate();
  const backgroundColor = isSelected ? "white" : "primary.800";
  const contentColor = isSelected ? "primary.800" : "white";

  const width = useBreakpointValue({
    lg: "175px",
    xl: "225px",
    "2xl": "275px",
  })!;

  const fontSize = {
    lg: "16px",
    xl: "20px",
    "2xl": "28px",
  };

  const handleLinkSelection = () => {
    navigate(link.path);
    controls.start("hidden").then(() => controls.start("visible"));
  };

  return (
    <HStack
      as={motion.div}
      variants={profileLinkVariants(width)}
      animate={controls}
      backgroundColor={backgroundColor}
      marginY="2px"
      w={width}
      py={fontSize}
      pl="4px"
      borderRightRadius="12px"
      cursor="pointer"
      onClick={handleLinkSelection}
      h={fontSize}
    >
      <Icon as={link.icon} color={contentColor} boxSize={fontSize} />
      <Text
        as={motion.p}
        variants={profileContentVariants}
        color={contentColor}
        fontSize={fontSize}
        userSelect="none"
      >
        {link.text}
      </Text>
    </HStack>
  );
};

const HorizontalLink: React.FC<ProfileLinkProps> = ({ link, isSelected }) => {
  const navigate = useNavigate();
  const fontSize = {
    base: "16px",
    md: "20px",
  };

  return (
    <HStack
      backgroundColor="primary.400"
      color="white"
      onClick={() => navigate(link.path)}
      cursor="pointer"
      flexGrow={1}
      justifyContent="center"
      borderEnd="1px solid white"
      fontSize={fontSize}
    >
      <Icon as={link.icon} />
      <Text as={motion.p} variants={profileContentVariants} userSelect="none">
        {link.text}
      </Text>
    </HStack>
  );
};

export default ProfileLinks;
