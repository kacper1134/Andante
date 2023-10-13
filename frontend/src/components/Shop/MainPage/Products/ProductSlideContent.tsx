import { Box, Text, VStack, HStack, Button, useToast, Spacer } from "@chakra-ui/react";
import { motion, useAnimationControls, Variants } from "framer-motion";
import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import noimage from "../../../../static/noimage.png";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../../../config/firebase-config";
import { useKeycloak } from "@react-keycloak/web";
import { useDispatch } from "react-redux";
import { cartActions } from "../../../../store/cart/cartSlice";
import useUserProfile from "../../../../hooks/useUserProfile";
import add_interaction, { InteractionType } from "../../../../functions/recommendation-functions";

type ProductSlideContentProps = {
  product: ProductOutputDTO;
  isDragOn: boolean;
};

const fontSize = {
  base: "13px",
  sm: "14px",
  md: "15px",
  lg: "16px",
  xl: "18px",
};

const ProductSlideContent = ({ product, isDragOn }: ProductSlideContentProps) => {
  const controls = useAnimationControls();
  const [image, setImage] = useState<string>(noimage);
  const { keycloak } = useKeycloak();
  const userProfile = useUserProfile();
  const dispatch = useDispatch();
  const toast = useToast();

  useEffect(() => {
    const fetchImage = async () => {
      if (product.variants.length > 0) {
        const downloadUrl = await getDownloadURL(ref(storage, product.variants[0].imageUrl));

        setImage(downloadUrl);
      }
    }

    fetchImage().catch(() => setImage(noimage));
  }, [product]);

  const variants: Variants = {
    animate: {
      scale: 1.1,
    },
    exit: {
      scale: 1.0,
    }
  };

  const start = useCallback(() => {
    controls.start("animate");
  }, [controls]);

  const end = useCallback(() => {
    controls.start("exit");
  }, [controls]);

  if(isDragOn) {
    end();
  }

  const addToCart = () => {
    dispatch(cartActions.addToCart({
      variant: product.variants[0],
      product: product,
      quantity: 1
    }));
    add_interaction(userProfile?.username!, product.id, InteractionType.CART);
    toast({
      title: 'Cart updated',
      description: `Product ${product.name} have been successfully added to your cart`,
      status: "success",
      duration: 9000,
      isClosable: true,  
    })
  }

  const canAddToCart = product.variants[0]?.availableQuantity > 0;

  const width = {
    base: "120px",
    sm: "150px",
    md: "180px",
    lg: "210px",
    xl: "240px",
  };

  const height = {
    base: "90px",
    sm: "112px",
    md: "135px",
    lg: "160px",
    xl: "180px",
  };

  return (
    <>
      <Box
        bgImage={`url(${image})`}
        bgRepeat="no-repeat"
        bgSize="cover"
        bgPosition="center"
        width={width}
        height={height}
        m="50px 5px 5px 5px"
      ></Box>
      <VStack spacing={{base: 3, md: 4}} width="100%" px="15px" flexGrow={1}>
        <Text
          as={Link}
          to={`product/${product.id}`}
          color="primary.200"
          width="100%"
          fontSize={{
            base: "14px",
            sm: "16px",
            md: "18px",
            lg: "19px",
            xl: "20px",
          }}
          textStyle="p"
          noOfLines={2}
        >
          {product.name}
        </Text>
        <Spacer />
        <HStack color="white" alignSelf="flex-start" fontSize={fontSize} justifyContent="space-between" w="inherit" pb="10px">
          <Text pr="10%" textStyle="p">${product.price}</Text>
          {keycloak.authenticated && <Button
          alignSelf="flex-start"
          color="white"
          colorScheme="purple"
          fontSize={fontSize}
          size="xs"
          rounded="xl"
          p="12px"
          as={motion.button}
          animate={controls}
          variants={variants}
          onHoverStart={() => start()}
          onHoverEnd={() => end()}
          textStyle="p"
          disabled={!canAddToCart}
          onClick={addToCart}
        >
          Add to cart
        </Button>}
        </HStack>
      </VStack>
    </>
  );
};

export default ProductSlideContent;
