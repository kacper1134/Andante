import { Box, Text, VStack, HStack, Button, useToast } from "@chakra-ui/react";
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

  return (
    <>
      <Box
        bgImage={`url(${image})`}
        bgRepeat="no-repeat"
        bgSize="cover"
        bgPosition="center"
        width="50%"
        height="50%"
        m="5"
      ></Box>
      <VStack spacing={{base: 3, md: 4}} width="100%" pl="15px">
        <Text
          as={Link}
          to={`product/${product.id}`}
          color="primary.200"
          width="100%"
          fontSize={{
            base: "16px",
            sm: "19px",
            md: "22px",
            lg: "26px",
            xl: "30px",
          }}
          textStyle="p"
        >
          {product.name}
        </Text>
        <HStack color="white" alignSelf="flex-start" fontSize={fontSize}>
          <Text pr="10%" textStyle="p">${product.price}</Text>
        </HStack>
        {keycloak.authenticated && <Button
          alignSelf="flex-start"
          color="white"
          colorScheme="purple"
          fontSize={fontSize}
          size="xs"
          rounded="xl"
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
      </VStack>
    </>
  );
};

export default ProductSlideContent;
