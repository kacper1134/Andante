import { useRef } from "react";
import { ChevronLeftIcon } from "@chakra-ui/icons";
import { Heading, useBreakpointValue, VStack, Text, Divider, HStack, Spacer, Button, Icon, useDisclosure, AlertDialog, AlertDialogOverlay, AlertDialogContent, AlertDialogHeader, AlertDialogBody, AlertDialogFooter } from "@chakra-ui/react";
import React, { Fragment } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import CartEntry, { CartEntryData } from "./CartEntry";
import { cartActions } from "../../store/cart/cartSlice";
import { FaRegSmileWink } from "react-icons/fa";
import { useTranslation } from "react-i18next";

export interface CartContentProps {
    cartItems: CartEntryData[]
}

const CartContent: React.FC<CartContentProps> = ({cartItems}) => {
    const { isOpen, onOpen, onClose } = useDisclosure();
    const cancelRef = useRef(null);
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const {t} = useTranslation();
    const headingSize = useBreakpointValue({
        base: "20px",
        md: "24px",
        lg: "28px",
        xl: "32px",
    })!;

    const textSize = useBreakpointValue({
        base: "10px",
        md: "11px",
        lg: "12px",
        xl: "14px"
    })!;

    const width = useBreakpointValue({
        base: 320,
        md: 500,
        lg: 550,
        xl: 800,
    })!;

    const cartWarning = t("orderPage.cart.content.warning");

    function clearCart() {
        onClose();
        dispatch(cartActions.clearCart());
    }
            
    return <>
    <VStack spacing="12px" maxW={width + "px"} alignSelf="center">
        <Heading color="primary.400" textStyle="h2" fontSize={headingSize} alignSelf="start">{t("orderPage.cart.content.title")}</Heading>
        <Text color="gray.600" textStyle="p" fontSize={textSize} alignSelf="start">{cartWarning}</Text>
        <Divider borderColor="primary.200" />
        {cartItems.map((cartItem, index) => 
        <Fragment key={index}>
            <CartEntry data={cartItem} baseSize={width} includeIcons />
            {index !== cartItems.length - 1 && <Divider borderColor="primary.300" />}
        </Fragment>)}
        {cartItems.length === 0 &&
        <HStack color="gray.500" alignSelf="start">
            <Text textStyle="p">{t("orderPage.cart.content.empty")}</Text>
            <Icon as={FaRegSmileWink} />
        </HStack> }
        <Divider borderColor="primary.300" />
        <HStack alignSelf="start" w={width}>
            <Text fontSize="14px" textStyle="p" color="gray.400" userSelect="none" cursor={cartItems.length > 0 ? "pointer" : "auto"} onClick={ cartItems.length > 0 ? onOpen : () => {}}>{t("orderPage.cart.content.clear")}</Text>
            <Spacer />
            <Button textStyle="p" leftIcon={<Icon as={ChevronLeftIcon} />} colorScheme="whiteAlpha" color="black" fontSize="14px" onClick={() => navigate("/shop")}>{t("orderPage.cart.content.back")}</Button>
        </HStack>
    </VStack>
    <AlertDialog leastDestructiveRef={cancelRef} isOpen={isOpen} onClose={onClose}>
            <AlertDialogOverlay>
                <AlertDialogContent textStyle="p">
                    <AlertDialogHeader fontSize="lg" fontWeight="bold">
                        {t("orderPage.cart.content.dialog.title")}
                    </AlertDialogHeader>
                    <AlertDialogBody>
                        {t("orderPage.cart.content.dialog.content")}
                    </AlertDialogBody>
                    <AlertDialogFooter>
                        <Button ref={cancelRef} onClick={onClose}>
                            {t("orderPage.cart.content.dialog.cancel")}
                        </Button>
                        <Button colorScheme="red" onClick={clearCart} ml={3}>
                            {t("orderPage.cart.content.dialog.clear")}
                        </Button>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialogOverlay>
    </AlertDialog>
    </>
}

export default CartContent;