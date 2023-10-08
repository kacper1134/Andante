import { Heading, VStack, HStack, useBreakpointValue, Text, Divider, Spacer, Button, useToast } from "@chakra-ui/react";
import { Dispatch, SetStateAction } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../store";

export interface CartSummaryProps {
    changeCurrentCardStep: Dispatch<SetStateAction<number>>,
}

const CartSummary: React.FC<CartSummaryProps> = ({changeCurrentCardStep}) => {
    const toast = useToast();
    const cartItems = useSelector((state: RootState) => state.cart.cartItems);
    const currency = useSelector((state: RootState) => state.cart.currency);
    const totalPrice = cartItems.map(item => item.quantity * item.variant.price).reduce((prev, next) => prev + next, 0);
    const totalDiscount = 0;

    const headingSize = useBreakpointValue({
        base: "20px",
        md: "24px",
        lg: "28px",
        xl: "32px",
    })!;

    function displayEmptyCartToast() {
        toast({
            title: 'Empty cart',
            description: 'Your cart is empty!',
            duration: 2500,
            status: "warning",
            isClosable: true,
        })
    }

    const width = useBreakpointValue({
        base: 320,
        md: 400,
    })!;

    const textSize = useBreakpointValue({
        base: "14px",
        md: "15px",
        lg: "16px",
        xl: "18px"
    })!;

    return <VStack alignSelf="center">
        <Heading color="primary.400" textStyle="h2" fontSize={headingSize} alignSelf="start">CART SUMMARY</Heading>
        <VStack p="12px" border="1px solid black" w={width}>
            <HStack w={width - 20} fontSize={textSize}>
                <VStack w="250px" spacing={0} textStyle="p">
                    <Text alignSelf="start">PRODUCTS VALUE</Text>
                    <Text alignSelf="start" color="gray.500">INCLUDING DISCOUNT</Text>
                </VStack>
                <VStack w="150px" spacing={0} textStyle="p">
                    <Text alignSelf="end">{currency}{totalPrice}</Text>
                    <Text alignSelf="end" color="gray.500">{currency}{totalDiscount}</Text>
                </VStack>
            </HStack>
            <Divider />
            <HStack w={width - 20} fontSize={textSize} textStyle="p">
                <Text alignSelf="start" fontWeight={600}>TOTAL PRICE</Text>
                <Spacer />
                <Text fontWeight={600}>{currency}{totalPrice - totalDiscount}</Text>
            </HStack>
        </VStack>
        <Button w={width} bg="primary.300" color="white" borderRadius={0} _hover={{bg: "primary.400"}} _active={{bg: "primary.400"}} onClick={totalPrice > 0 ? () => changeCurrentCardStep(1) : () => displayEmptyCartToast()}>Proceed To Place Order</Button>
    </VStack>
}

export default CartSummary;