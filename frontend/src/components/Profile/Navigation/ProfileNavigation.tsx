import React, { useEffect, useState } from "react";
import { Box, Avatar, useBreakpointValue, VStack, Text, HStack, Icon, Button } from "@chakra-ui/react";
import { motion } from 'framer-motion';
import { profileContentVariants, profileNavigationVariants } from "./ProfileNavigationVariants";
import { IoLocationOutline } from "react-icons/io5";
import ProfileLinks from "./ProfileLink/ProfileLinks";
import { AiFillEdit } from "react-icons/ai";
import { ImExit } from "react-icons/im";
import { useDispatch, useSelector } from "react-redux";
import { innerActions } from "../../../store/inner/innerSlice";
import { useKeycloak } from "@react-keycloak/web";
import { RootState } from "../../../store";
import { UserDetails } from "../../../utils/KeycloakUtils";
import { useNavigate } from "react-router-dom";
import useUserProfile from "../../../hooks/useUserProfile";
import { authActions } from "../../../store/auth/auth-slice";
import { cartActions } from "../../../store/cart/cartSlice";
import useGetFirebaseImage from "../../../hooks/useGetFirebaseImage";

export interface ProfileNavigationProps {
    navbarHeight: string,
    isSidebarOpened: boolean,
};

const ProfileNavigation: React.FC<ProfileNavigationProps> = ({ navbarHeight, isSidebarOpened }) => {
    return (
        <>
            {isSidebarOpened && <Sidebar navbarHeight={navbarHeight} />}
        </>
    )
};

interface SidebarProps {
    navbarHeight: string,
};

const Sidebar: React.FC<SidebarProps> = ({ navbarHeight }) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { keycloak, initialized } = useKeycloak();
    const userDetails: UserDetails | undefined = useSelector((state: RootState) => state.auth.userDetails);
    const userProfile = useUserProfile();
    const getImage = useGetFirebaseImage();
    const [image, setImage] = useState("");

    const sidebarWidth = useBreakpointValue({
        base: 0,
        lg: 200,
        xl: 250,
        '2xl': 300,
    })!;

    const fontSize = useBreakpointValue({ lg: 16, xl: 20, '2xl': 24 })!;

    const avatarSize = {
        lg: "100px",
        xl: '120px',
        '2xl': '144px',
    };

    const buttonStyle = {
        fontSize: `${fontSize - 2}px`,
        backgroundColor: "white",
        color: "primary.800",
        borderRadius: "20px",
        width: "10em",
    }
    
    useEffect(() => {
        dispatch(innerActions.setSidebarWidth(sidebarWidth));
        if(initialized && keycloak.authenticated && userProfile?.imageUrl !== undefined) {
            getImage(userProfile?.imageUrl ?? "").then((url) => setImage(url))
            .catch(() => setImage(""));
        }
    }, [dispatch, getImage, initialized, keycloak.authenticated, sidebarWidth, userProfile?.imageUrl]);

    const logout = () => {
        keycloak.logout({redirectUri: "http://" + window.location.host + "/home"});
        dispatch(authActions.queueLogout());
        dispatch(cartActions.clearCart());
    }

    return (
        <Box as={motion.div}
            variants={profileNavigationVariants(sidebarWidth + "px")}
            initial="initial"
            animate="animate"
            exit="exit"
            backgroundColor="primary.800"
            h="100vh"
            position="sticky"
            top="0"
            w={sidebarWidth}
            pt={navbarHeight}>
            <VStack as={motion.div} variants={profileContentVariants} color="white" textAlign="center" wordBreak="break-word">
                <Avatar boxSize={avatarSize} mb={{ lg: '4px', xl: '6px', '2xl': '8px' }} src={image} />
                <Text textStyle="h3" fontSize={fontSize + "px"}>{userDetails?.personal.name ?? "Unknown"} {userDetails?.personal.surname ?? "Unknown"}</Text>
                <Text textStyle="p" fontSize={(fontSize - 2) + "px"} fontWeight="thin" lineHeight="90%">Joined {userDetails?.personal.accountCreated.monthLong ?? "Unknown"} {userDetails?.personal.accountCreated.year ?? "Unknown"}</Text>
                <HStack textStyle="p" fontSize={(fontSize - 6) + "px"} fontWeight='hairline'>
                    <Icon as={IoLocationOutline} boxSize={fontSize + "px"} />
                    <Text >{userDetails?.delivery.city ?? "Unknown"},{userDetails?.delivery.country ?? "Unknown"}</Text>
                </HStack>
                <Button leftIcon={<AiFillEdit/>} sx={buttonStyle} onClick={() => navigate("profile/edit")}>
                    Edit account
                </Button> 
                <Button 
                    leftIcon={<ImExit />}
                    sx={buttonStyle}
                    onClick={logout}>
                    Sign Out
                </Button>
                <ProfileLinks isSidebarOpened={true} />
            </VStack>
        </Box>
    )
}


export default ProfileNavigation;