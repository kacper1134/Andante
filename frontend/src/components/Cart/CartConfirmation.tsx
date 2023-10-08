import { Heading, useBreakpointValue, VStack, Text, Divider, HStack, Button } from "@chakra-ui/react";
import { DateTime } from "luxon";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { RootState } from "../../store";
import { useLazyGetOrderEntriesQuery, useLazyGetOrderQuery } from "../../store/api/order-api-slice";
import { OrderResultType } from "../Profile/History/OrderHistory";
import CartEntry, { CartEntryData } from "./CartEntry";
import { DeliveryMethod, PaymentMethodData } from "./CartForms";

export interface OrderDetails {
    personal: {
        name: string,
        surname: string,
        emailAddress: string,
        phoneNumber: string,
    },
    location: {
        country: string,
        city: string,
        street: string,
        buildingNumber: string,
        flatNumber?: string,
        postalCode: string,
    },
}

export interface OrderData {
    orderId?: number,
    orderDate?: DateTime,
    deliveryDate?: DateTime,
    orderDetails: OrderDetails,
    deliveryMethod: DeliveryMethod,
    paymentMethod: PaymentMethodData,
}

export interface CartConfirmationProps {
    
}

const CartConfirmation: React.FC<CartConfirmationProps> = () => {
    const currency = useSelector((state: RootState) => state.cart.currency);
    const navigate = useNavigate();
    const orderData = useSelector((state: RootState) => state.cart.orderData);
    const [order, setOrder] = useState<OrderResultType>();
    const [entries, setEntries] = useState<CartEntryData[]>([]);
    const totalPrice = entries.reduce((acc, current) => acc + current.quantity * current.variant.price, 0);
    const totalDiscount = 0;
    const [fetchOrder] = useLazyGetOrderQuery();
    const [fetchOrderEntries] = useLazyGetOrderEntriesQuery();
    const finalPrice = totalPrice - totalDiscount + orderData.deliveryMethod.price + orderData.paymentMethod.cost;

    const baseSize = useBreakpointValue({
        base: 320,
        md: 600,
        lg: 800,
        xl: 1000,
        '2xl': 1200,
    })!;
    const headingSize = useBreakpointValue({
        base: "16px",
        md: "24px",
        lg: "28px",
        xl: "32px",
    })!;

    const fontSize = useBreakpointValue({
        base: 14,
        md: 15,
        lg: 16,
        xl: 17,
        '2xl': 18,
    })!;

    useEffect(() => {
        if(orderData.orderId) {
            fetchOrder(orderData.orderId).then((order) => {
                setOrder(order.data);
                fetchOrderEntries((order.data as any).id).then((entries) => {
                    setEntries(entries.data!.map(entry => {return {
                        variant: entry.productVariant,
                        quantity: entry.quantity,
                    }}));
                })
            })
        }
    }, [fetchOrder, fetchOrderEntries, orderData.orderId])

    const paymentMethod = order ? (order as any).paymentMethod : undefined;
    console.log(order);
    return <VStack w={baseSize + "px"} margin="auto" spacing={0}>
        <Heading color="primary.400" textStyle="h2" fontSize={headingSize}>Thank You For Submitting Your Order</Heading>
        <Text color="gray.500" textStyle="p" fontSize={fontSize - 4} textAlign="center" pt={fontSize / 3} pb={fontSize / 3}>We have sent confirmation of the order to the provided e-mail address</Text>
        <Divider borderColor="gray.600" />
        <Text color="gray.500" textStyle="p" fontSize={fontSize - 2} alignSelf="start" pt="4px">Order Number</Text>
        <Text color="black" textStyle="p" fontSize={fontSize + 2} alignSelf="start">{orderData.orderId}</Text>
        <Text color="gray.500" textStyle="p" fontSize={fontSize - 2} alignSelf="start" pt="4px">Order Date</Text>
        <Text color="black" textStyle="p" fontSize={fontSize + 2} alignSelf="start">{orderData.orderDate?.toLocaleString(DateTime.DATETIME_MED_WITH_SECONDS)}</Text>
        <Text color="gray.500" textStyle="p" fontSize={fontSize - 2} alignSelf="start" pt="4px">Order Status</Text>
        <Text color="black" textStyle="p" fontSize={fontSize + 2} alignSelf="start" pb="4px">New Order</Text>
        <Divider borderColor="gray.600" />
        <HStack w="inherit" justifyContent="space-between" pt="16px">
            <VStack alignSelf="start">
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start">Full Name</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.personal.name} {orderData.orderDetails.personal.surname}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start" pt="8px">Email Address</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.personal.emailAddress}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start" pt="8px">Phone Number</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.personal.phoneNumber}</Text>
            </VStack>
            <VStack alignSelf="start">
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start">Country</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.location.country}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start" pt="8px">City</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.location.city}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start" pt="8px">Street</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.location.street}</Text>
            </VStack>
            <VStack alignSelf="start">
            <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start">Building Number</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.location.buildingNumber}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start" pt="8px">Flat Number</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{!orderData.orderDetails.location.flatNumber || orderData.orderDetails.location.flatNumber === "" ? "Not Provided" : orderData.orderDetails.location.flatNumber!}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start" pt="8px">Postal Code</Text>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.orderDetails.location.postalCode}</Text>
            </VStack>
        </HStack>
        <HStack w="inherit" justifyContent="space-between" pt="16px">
            <VStack>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.deliveryMethod.provider}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start">Expected to be delivered on {orderData.deliveryDate?.toLocaleString(DateTime.DATE_MED)}</Text>
            </VStack>
            <Text fontSize={fontSize} textStyle="p" alignSelf="start">{currency}{orderData.deliveryMethod.price}</Text>
        </HStack>
        <HStack w="inherit" justifyContent="space-between" pt="16px">
            <VStack>
                <Text fontSize={fontSize} textStyle="p" alignSelf="start">{orderData.paymentMethod.provider}</Text>
                <Text fontSize={fontSize - 2} textStyle="p" color="gray.500" alignSelf="start">{orderData.paymentMethod.description}</Text>
            </VStack>
            <Text fontSize={fontSize} textStyle="p" alignSelf="start">{currency}{orderData.paymentMethod.cost}</Text>
        </HStack>
        {(paymentMethod && paymentMethod === "PayU") && <HStack w="inherit" justifyContent="space-between" pt="16px" pb="16px">
            <Text fontSize={fontSize} textStyle="p" alignSelf="start">Awaiting Payment</Text> 
            <Button onClick={() => navigate(`./payment?orderId=${orderData.orderId}`)} bg="primary.300" fontSize={fontSize} borderRadius={0} color="white" _hover={{bg: "primary.400"}} _active={{bg: "primary.400"}}>Proceed To Payment</Button>
        </HStack>}
        <Divider borderColor="gray.500" pb="10px" />
        {entries.map((cartItem, index) => <CartEntry data={cartItem} baseSize={baseSize} includeIcons={false} key={index} />)}
        <Divider borderColor="gray.500" pt="10px" />
        <VStack w={baseSize + "px"} spacing={0} py="8px">
            <HStack w="inherit" justifyContent="space-between" textStyle="p" fontSize={fontSize}>
                <Text fontWeight={600}>Products Value</Text>
                <Text fontWeight={600} >{currency}{totalPrice}</Text>
            </HStack>
            <HStack w="inherit" justifyContent="space-between" textStyle="p" fontSize={fontSize} color="green.500">
                <Text fontWeight={600}>Delivery Cost</Text>
                <Text fontWeight={600}>{currency}{orderData.deliveryMethod.price}</Text>
            </HStack>
            <HStack w="inherit" justifyContent="space-between" textStyle="p" fontSize={fontSize} color="green.500">
                <Text fontWeight={600}>Payment Cost</Text>
                <Text fontWeight={600}>{currency}{orderData.paymentMethod.cost}</Text>
            </HStack>
        </VStack>
        <Divider borderColor="gray.500" />
        <VStack w={baseSize + "px"} spacing={0} py="8px">
            <HStack w="inherit" justifyContent="space-between"  textStyle="p" fontSize={fontSize}>
                <Text fontWeight={600}>Total Price</Text>
                <Text fontWeight={600}>{currency}{finalPrice}</Text>
            </HStack>
            <HStack w="inherit" justifyContent="space-between" textStyle="p" fontSize={fontSize} color="green.500">
                <Text fontWeight={600}>Including Discount</Text>
                <Text fontWeight={600}>{currency}{totalDiscount}</Text>
             </HStack>
        </VStack>
    </VStack>
}

export default CartConfirmation;