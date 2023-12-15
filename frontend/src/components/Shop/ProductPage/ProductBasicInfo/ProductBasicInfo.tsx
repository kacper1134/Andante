import {
  useBreakpointValue,
  Text,
  HStack,
  VStack,
  Spacer,
  Button,
  useToast,
} from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import ProductAverageRating from "./ProductAverageRating";
import { imageHeight } from "./ProductDimensions";
import ProductLikeButton from "./ProductLikeButton";
import { cartActions } from "../../../../store/cart/cartSlice";
import { ProductVariantOutputDTO } from "../../../../store/api/result/dto/product/variant/ProductVariantOutputDTO";
import { getUserDetails, UserDetails } from "../../../../utils/KeycloakUtils";
import add_interaction, {
  InteractionType,
} from "../../../../functions/recommendation-functions";
import useUserProfile from "../../../../hooks/useUserProfile";
import { RootState } from "../../../../store";
import { useNavigate } from "react-router-dom";
import BuyNowModal from "../../MainPage/Products/BuyNowModal";
import { useTranslation } from "react-i18next";

const headerSize = {
  base: "14px",
  sm: "14px",
  md: "16px",
  lg: "16px",
  xl: "24px",
  "2xl": "30px",
};

const priceFontSize = {
  base: "12px",
  sm: "14px",
  md: "16px",
  lg: "18px",
  xl: "20px",
  "2xl": "22px",
};

const fontSize = {
  base: "10px",
  sm: "11px",
  md: "12px",
  lg: "10px",
  xl: "13px",
  "2xl": "16px",
};

export interface ProductBasicInfoProps {
  data: ProductOutputDTO;
  selectedVariant?: ProductVariantOutputDTO;
  setAreFeaturesOpen: Dispatch<SetStateAction<boolean>>;
  areFeaturesOpen: boolean;
}

const ProductBasicInfo: React.FC<ProductBasicInfoProps> = ({
  data,
  selectedVariant,
  setAreFeaturesOpen,
  areFeaturesOpen,
}) => {
  const { keycloak } = useKeycloak();
  const horizontalWidth = parseInt(useBreakpointValue(imageHeight)!) * 1.5;
  const starSize = parseInt(useBreakpointValue(headerSize)!);
  const [userDetails, setUserDetails] = useState<UserDetails>();
  const userProfile = useUserProfile();
  const [isLiked, setIsLiked] = useState(false);
  const dispatch = useDispatch();
  const toast = useToast();
  const {t} = useTranslation();  
  const width = {
    base: horizontalWidth,
    lg: "320px",
    xl: "440px",
    "2xl": "560px",
  };

  useEffect(() => {
    if (keycloak.idTokenParsed) {
      const details = getUserDetails(keycloak.idTokenParsed);

      setUserDetails(details);

      const isObserved = data.observers.includes(details.personal.emailAddress);

      setIsLiked(isObserved);
    }
  }, [keycloak, data]);

  const addToCart = () => {
    if (selectedVariant) {
      dispatch(
        cartActions.addToCart({
          variant: selectedVariant,
          product: data,
          quantity: 1,
        })
      );
      add_interaction(userProfile?.username!, data.id, InteractionType.CART);
      toast({
        title: t("shopPage.productPage.addToCart.modal.title"),
        description: `${t("shopPage.productPage.addToCart.modal.contentFirst")} ${data.name} ${t("shopPage.productPage.addToCart.modal.contentSecond")}`,
        status: "success",
        duration: 9000,
        isClosable: true,
      });
    }
  };

  const navigate = useNavigate();

  const [isOpen, setIsOpen] = useState(false);

  const buyNow = (value: SetStateAction<number>) => {
    if (selectedVariant) {
      navigate(`/cart?startCartStep=2`);
    }
  };

  const openConfirmationModal = () => {
    if (selectedVariant) {
      setIsOpen(true);
      dispatch(
        cartActions.addBuyNowItem({
          variant: selectedVariant,
          product: data,
          quantity: 1,
        })
      );
    }
  };

  const canAddToCart = selectedVariant
    ? selectedVariant.availableQuantity >= 0
    : false;

  const alternativeVersionOfInterface = useSelector(
    (state: RootState) => state.auth.alternativeVersionOfInterface
  );
  
  return (
    <VStack
      alignItems="flex-start"
      width={width}
      minHeight={imageHeight}
      pl={{ lg: "3%" }}
    >
      <Text fontSize={headerSize} textStyle="h1">
        {data.name}
      </Text>
      <HStack>
        <Text fontSize={headerSize} mb="2%" textStyle="h1">
          {t(data.productType)}
        </Text>
        {keycloak.authenticated && (
          <ProductLikeButton
            productId={data.id}
            userEmail={userDetails?.personal.emailAddress}
            isLiked={isLiked}
            setIsLiked={setIsLiked}
            fontSize={priceFontSize}
          />
        )}
      </HStack>
      <ProductAverageRating
        product={data}
        fontSize={priceFontSize}
        starSize={starSize}
        areFeaturesOpen={areFeaturesOpen}
        setAreFeaturesOpen={setAreFeaturesOpen}
      />
      <HStack fontSize={priceFontSize} pt="1%" spacing={6}>
        <Text textStyle="p" fontWeight="bold">
          ${selectedVariant ? selectedVariant.price : data.price}
        </Text>
      </HStack>
      <Text fontSize={fontSize} textStyle="p">
        {data.description}
      </Text>
      <Spacer />
      {keycloak.authenticated && (
        <HStack width="100%">
          <Button
            width={alternativeVersionOfInterface ? "50%" : "100%"}
            colorScheme="purple"
            fontWeight="bold"
            py="5%"
            fontSize={priceFontSize}
            textStyle="p"
            disabled={!canAddToCart}
            onClick={addToCart}
          >
            {t("shopPage.productPage.addToCart.button")}
          </Button>
          {alternativeVersionOfInterface && (
            <Button
              width="50%"
              colorScheme="whatsapp"
              fontWeight="bold"
              py="5%"
              fontSize={priceFontSize}
              textStyle="p"
              disabled={!canAddToCart}
              onClick={() => openConfirmationModal()}
            >
              {t("shopPage.productPage.buyNow.button")}
            </Button>
          )}
        </HStack>
      )}
      <BuyNowModal isOpen={isOpen} setIsOpen={setIsOpen} buyNow={buyNow} />
    </VStack>
  );
};

export default ProductBasicInfo;
