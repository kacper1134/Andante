import { useEffect, useState } from "react";
import { HStack, VStack, Icon, useToken, Image, Text, NumberInput, NumberInputField, NumberInputStepper, NumberIncrementStepper, NumberDecrementStepper, Spacer, useBreakpointValue } from "@chakra-ui/react";
import { BsSuitHeart, BsSuitHeartFill } from "react-icons/bs";
import { IoMdClose } from "react-icons/io";
import { useDispatch, useSelector } from "react-redux";
import { cartActions } from "../../store/cart/cartSlice";
import { RootState } from "../../store";
import { ProductVariantOutputDTO } from "../../store/api/result/dto/product/variant/ProductVariantOutputDTO";
import noimage from "../../static/noimage.png";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../config/firebase-config";
import { ProductOutputDTO } from "../../store/api/result/dto/product/base/ProductOutputDTO";
import { useNavigate } from "react-router-dom";
import { useChangeObservationStatusMutation } from "../../store/api/productSlice";
import { useKeycloak } from "@react-keycloak/web";
import { getUserDetails } from "../../utils/KeycloakUtils";

export interface CartEntryData {
    productId?: number;
    variant: ProductVariantOutputDTO,
    product?: ProductOutputDTO,
    quantity: number,
};

export interface CartEntryProps {
    data: CartEntryData,
    baseSize: number,
    includeIcons: boolean,
}

const CartEntry: React.FC<CartEntryProps> = ({ data, baseSize, includeIcons }) => {
    const [image, setImage] = useState<string>(noimage);
    const { keycloak } = useKeycloak();
    const [isLiked, setIsLiked] = useState<boolean>(false);
    const navigate = useNavigate();
    const [changeObservationStatus] = useChangeObservationStatusMutation();

    useEffect(() => {
        const fetchData = async () => {
            const downloadUrl = await getDownloadURL(ref(storage, data.variant.thumbnailUrl));

            setImage(downloadUrl);
        }

        fetchData().catch(() => setImage(noimage));
    }, [data]);

    useEffect(() => {
        if (keycloak.idTokenParsed) {
            const userDetails = getUserDetails(keycloak.idTokenParsed);

            setIsLiked(data.product ? data.product.observers.includes(userDetails.personal.emailAddress) : false);
        }
    }, [keycloak]);

    const changeStatus = () => {
        if (data.product && keycloak.idTokenParsed) {
            const userDetails = getUserDetails(keycloak.idTokenParsed);

            changeObservationStatus({productId: data.product.id, email: userDetails.personal.emailAddress})
            .then(() => setIsLiked(!isLiked)); 
        }
    }

    const dispatch = useDispatch();
    const currency = useSelector((state: RootState) => state.cart.currency); 
    const heartColor = useToken("colors", "secondary.500");
    const iconSize = useBreakpointValue({
        base: "20px",
        md: "24px",
        lg: "28px",
    })!;

    const height = useBreakpointValue({
        base: 150,
        md: 175,
        lg: 200,
    })!;

    const headerSize = useBreakpointValue({
        base: "14px",
        md: "15px",
        lg: "16px",
        xl: "17px",
        '2xl': "18px",
    })!;
    
    const textSize = useBreakpointValue({
        base: "12px",
        md: "13px",
        lg: "14px",
        xl: "15px",
        '2xl': "16px",
    })!;

    function changeCartQuantity(newQuantity: number) {
        dispatch(cartActions.changeProductQuantity({id: data.variant.id, newQuantity: newQuantity}))
    }

    function removeFromCart() {
        dispatch(cartActions.removeFromCart(data.variant.id));
    }
    
    return <HStack w={baseSize + "px"} h={height + "px"} alignSelf="start">
        <Image w={baseSize / 4 + "px"} h={height * 7 / 8 + "px"} src={image} objectFit="cover" alignSelf="end" />
        <VStack alignSelf="center" h={height + "px"} w={baseSize /2 + "px"}>
            <Text textStyle="p" fontWeight={600} alignSelf="start" fontSize={headerSize} userSelect="none" cursor="pointer" onClick={() => navigate(`/shop/product/${data.product?.id}`)}>{data.variant.productName}</Text>
            <Text textStyle="p" fontSize="14px" color="gray.600" alignSelf="start" overflow="hidden">{data.product?.description}</Text>
            <Spacer />
            <NumberInput value={data.quantity} min={1} borderColor="primary.200" onChange={(value) => changeCartQuantity(Number(value))} w={baseSize / 4 + "px"} isDisabled={!includeIcons} alignSelf="start">
                <NumberInputField _focusVisible={{outline: "none", borderColor: "primary.400"}} _hover={{borderColor: "primary.400"}} />
                <NumberInputStepper>
                    <NumberIncrementStepper color="primary.400" />
                    <NumberDecrementStepper color="primary.400" />
                </NumberInputStepper>
            </NumberInput>
        </VStack>
        <VStack alignSelf="center" h={height + "px"}>
            <Text textStyle="p" fontWeight={600} fontSize={textSize} alignSelf="start">{`${data.quantity} x ${currency}${data.variant.price}`}</Text>
        </VStack>
        <Spacer />
        <VStack alignSelf="center" h={height + "px"}>
            <Text textStyle="p" fontWeight={800} fontSize={headerSize} alignSelf="start">{currency}{data.variant.price * data.quantity}</Text>
            <Spacer />
            {includeIcons &&
            <>
                <Icon as={isLiked ? BsSuitHeartFill : BsSuitHeart} onClick={changeStatus} color={heartColor} boxSize={iconSize} cursor="pointer" />
                <Icon as={IoMdClose} boxSize={iconSize} cursor="pointer" onClick={removeFromCart} />
            </>}
        </VStack>
    </HStack>
}

export default CartEntry;