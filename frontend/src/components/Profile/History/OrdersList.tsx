import { HStack, useBreakpointValue, VStack, Image, Text, Divider, useToken, Icon, Center } from "@chakra-ui/react";
import { DateTime } from "luxon";
import { OrderStatus } from "../../../enums/OrderStatus";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { BsDot } from "react-icons/bs";
import { LinkVariants } from "./Variants";
import generateInvoice from "./InvoiceGenerator";
import { useState } from "react";
import CancelOrderModal from "./CancelOrderModal";
import { CartEntryData } from "../../Cart/CartEntry";
import noimage from "../../../static/noimage.png";
import { useFirebase } from "../../../hooks/useFirebase";

type OrdersListProps = {
    orders: OrderDetails[];
    setReload: React.Dispatch<React.SetStateAction<boolean>>;
}

const OrdersList = ({orders, setReload}: OrdersListProps) => {
    const headerFontSize = {
        base: "15px",
        sm: "20px",
        md: "25px",
        lg: "30px",
        xl: "35px",
    }

    return <VStack as={Center} spacing="24px" h="100%">
        {orders.length === 0 && <Text textStyle="h1" color="primary.600" fontSize={headerFontSize}>You have no orders of a given type!</Text>}
        {orders.map((order, index) => <OrderCard key={index} data={order} setReload={setReload} />)}
    </VStack>;
};

export interface OrderItem {
    image: string,
    id: number,
    name: string,
    unitPrice: number,
    quantity: number,
}

export interface Address {
    country: string,
    city: string,
    street: string,
    postalCode: string,
}

export interface PersonalDetails {
    id?: number,
    name: string,
    surname: string,
    email: string,
    phone: string
}

function getShortenedAddress(address: Address): string {
    return address.city + ", " + address.country;
}

export interface OrderDetails {
    orderId: number,
    orderImage?: string,
    orderTitle?: string,
    deliveryCost?: number,
    deliveryMethod?: string,
    paymentMethod?: string,
    totalCost?: number,
    orderedItems: CartEntryData[],
    addressedDetails: PersonalDetails,
    orderDate: DateTime,
    locationId?: number,
    deliveryLocationId?: number,
    paymentCost?: number,
    shippingAddress: Address,
    estimatedShipDate: DateTime
    orderStatus: OrderStatus,
    orderCurrency: string,
}

export interface OrderCardProps {
    data: OrderDetails,
    setReload: React.Dispatch<React.SetStateAction<boolean>>,
}

export type UpdateOrderType = {
    id: number;
    deliveryCost: number;
    deliveryMethod: string;
    paymentMethod: string;
    clientEmail: string;
    clientId: number;
    locationId: number;
    deliveryLocationId: number,
    status: string;
    paymentCost: number;
    totalCost: number;
    orderEntriesIds: number[];
}

const OrderCard: React.FC<OrderCardProps> = ({data, setReload}) => {
    const navigate = useNavigate();
    const primary400 = useToken("colors", "primary.400");
    const gray300 = useToken("colors", "gray.300");
    const orderCardWidth = useBreakpointValue({
        base: "350px",
        md: "450px",
        lg: "700px",
        xl: "800px",
        '2xl': "1000px",    
    });

    const [isModalOpen, setIsModalOpen] = useState(false);
    const image = useFirebase(data.orderImage!, noimage);
    
    return <VStack w={orderCardWidth} borderRadius="16px" boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)" py="8px">
        <HStack alignSelf="start" px="8px">
            <Image src={image} w="32px" h="32px" objectFit="contain" mr="4px" />
            <Text textStyle="h3" color="primary.400" fontSize="20px" noOfLines={2}>{data.orderTitle}</Text>
        </HStack>
        <Divider borderColor="gray.400"/>
        <HStack alignSelf="start" px="24px" spacing={{base: "24px", lg: "80px"}}>
            <VStack spacing={0}>
                <Text textStyle="p" color="gray.400" fontSize="16px" fontWeight={800}>Order #</Text>
                <Text textStyle="p" color="black" fontSize="16px" alignSelf="start">{data.orderId}</Text>
            </VStack>
            <VStack spacing={0}>
                <Text textStyle="p" color="gray.400" fontSize="16px" fontWeight={800}>Date Purchased</Text>
                <Text textStyle="p" color="black" fontSize="16px" alignSelf="start">{data.orderDate.toLocaleString(DateTime.DATE_MED)}</Text>
            </VStack>
            <VStack spacing={0}>
                <Text textStyle="p" color="gray.400" fontSize="16px" fontWeight={800}>Shipping Address</Text>
                <Text textStyle="p" color="black" fontSize="16px" alignSelf="start">{getShortenedAddress(data.shippingAddress)}</Text>
            </VStack>
            <VStack spacing={0}>
                <Text textStyle="p" color="gray.400" fontSize="16px" fontWeight={800}>Order Value</Text>
                <Text textStyle="p" color="black" fontSize="16px" alignSelf="start">{data.orderCurrency}{data.totalCost}</Text>
            </VStack>
        </HStack>
        <Divider borderColor="gray.400" />
        <HStack spacing="16px" alignSelf="start" pl="24px" w="inherit">
            <Text textStyle="p" fontWeight="800" color="gray.400" fontSize="14px" w="40px">QTY</Text>
            <Text textStyle="p" fontWeight="800" color="gray.400" fontSize="14px" pl="7px">Name</Text>
        </HStack>
        {data.orderedItems.map((orderedItem, index) => 
            <HStack key={index} spacing="16px" alignSelf="center" pl="16px" borderBottom={data.orderedItems.length -1 === index ? '' : `1px solid ${gray300}`} w="calc(100% - 32px)">
                <Text textStyle="p" color="black" fontSize="16px" w="40px" textAlign="center">{orderedItem.quantity}</Text>
                <Text as={motion.p} whileHover={{color: primary400}} textStyle="p" textAlign="start" color="black" fontSize="16px" userSelect="none" cursor="pointer" onClick={() => navigate(`/shop/product/${orderedItem.productId}`)}>{orderedItem.variant.productName}</Text>
            </HStack>
            )}
        <Divider borderColor="gray.400" />
        <HStack px="24px" spacing={{base: "48px", lg: "80px"}} w="inherit" alignSelf="start">
            <VStack spacing={0}>
                <Text textStyle="p" color="gray.400" fontSize="16px" fontWeight={800}>Order Status</Text>
                <Text textStyle="p" color="black" fontSize="16px" alignSelf="start">{OrderStatus[data.orderStatus].charAt(0).toUpperCase() + OrderStatus[data.orderStatus].substring(1).toLowerCase()}</Text>
            </VStack>
            <VStack spacing={0}>
                <Text textStyle="p" color="gray.400" fontSize="16px" fontWeight={800}>Estimated Ship Date</Text>
                <Text textStyle="p" color="black" fontSize="16px" alignSelf="start">{data.estimatedShipDate.toLocaleString(DateTime.DATE_MED)}</Text>
            </VStack>
        </HStack>
        
        { (data.orderStatus === OrderStatus.NEW) && <>
        <Divider borderColor="gray.400" />
        <HStack userSelect="none">
            {data.paymentMethod === "PayU" && <Text as={motion.p} variants={LinkVariants} whileHover="hover" textStyle="p" color="gray.500" fontSize="14px" cursor="pointer" onClick={() => navigate(`../../cart/payment?orderId=${data.orderId}`)}>PAY NOW</Text>}
            {data.paymentMethod === "PayU" && <Icon as={BsDot} boxSize="14px" color="gray.500" />}
            <Text as={motion.p} variants={LinkVariants} whileHover="hover" textStyle="p" color="gray.500" fontSize="14px" cursor="pointer" onClick={() => generateInvoice(data, true)}>INVOICE</Text>
            <Icon as={BsDot} boxSize="14px" color="gray.500" />
            <Text as={motion.p} variants={LinkVariants} whileHover="hover" textStyle="p" color="gray.500" fontSize="14px" cursor="pointer" onClick={() => setIsModalOpen(true)}>CANCEL ORDER</Text>
        </HStack>
        </>
        }
        {data.orderStatus === OrderStatus.COMPLETED && <>
        <Divider borderColor="gray.400" />
        <HStack userSelect="none">
            <Text as={motion.p} variants={LinkVariants} whileHover="hover" textStyle="p" color="gray.500" fontSize="14px" cursor="pointer" onClick={() => generateInvoice(data, true)}>INVOICE</Text>
        </HStack></>}
        <CancelOrderModal isOpen={isModalOpen} setIsOpen={setIsModalOpen} order={data} setReload={setReload} />     
    </VStack>
}

export { OrdersList, OrderCard };