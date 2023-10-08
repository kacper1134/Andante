import { VStack, Image, HStack, Text, Button, useToast } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import StarRating from "./StarRating";
import noimage from "../../../static/noimage.png";
import { useEffect, useState } from "react";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../../config/firebase-config";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { cartActions } from "../../../store/cart/cartSlice";
import { useKeycloak } from "@react-keycloak/web";
import add_interaction, { InteractionType } from "../../../functions/recommendation-functions";
import useUserProfile from "../../../hooks/useUserProfile";

type NewItemProps = {
  product: ProductOutputDTO;
};

const NewItem = ({ product }: NewItemProps) => {
  const [image, setImage] = useState<string>(noimage);
  const { keycloak } = useKeycloak();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const toast = useToast();
  const userProfile = useUserProfile();

  useEffect(() => {
    const fetchImage = async () => {
      const url = product.variants.length > 0 ? product.variants[0].thumbnailUrl : undefined;

      const downloadUrl = await getDownloadURL(ref(storage, url));

      setImage(downloadUrl);
    }

    fetchImage().catch(() => setImage(noimage));
  }, [product]);

  const addToCart = () => {
    if (product.variants.length === 0 || product.variants[0].availableQuantity === 0) {
      toast({
        title: "Product is not available",
        description: `Product ${product.name} is unfortunatelly not available right now. Be sure to check it out later.`,
        status: "error",
        duration: 9000,
        isClosable: true,
      })
    } else {
      dispatch(cartActions.addToCart({
        variant: product.variants[0], 
        product: product,
        quantity: 1}));
      add_interaction(userProfile?.username!, product.id, InteractionType.CART);
      toast({
        title: 'Cart updated',
        description: `Product ${product.name} have been successfully added to your cart`,
        status: "success",
        duration: 9000,
        isClosable: true,  
      })  
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      whileInView={{ opacity: 1 }}
      transition={{ duration: 3, delay: 0.2 }}
      viewport={{ once: true }}
    >
      <VStack m={15} mb={100}>
        <Image
          src={`${image}`}
          boxSize={{ base: "200px", sm: "250px" }}
          userSelect="none"
          bg="white"
          fit="cover"
        />
        <Text color="primary.200" 
          w={{base: "200px", sm: "250px"}}
          textAlign="center"
          fontSize={{ base: "2xl", sm: "3xl" }} 
          textStyle="h1" 
          userSelect="none" 
          cursor="pointer" 
          onClick={() => navigate(`/shop/product/${product.id}`)}>
          {product.name}
        </Text>
        <StarRating rating={product.averageRating} />
        <HStack pt="2" w ={{base: "200px", sm: "250px"}} justifyContent="space-around">
          <Text
            userSelect="none"
            color="primary.200"
            fontStyle="italic"
            fontWeight="bold"
            fontSize={{ base: "lg", sm: "2xl" }}
            textStyle="p"
          >
            ${product.price}
          </Text>
            {keycloak.authenticated && <Button
              backgroundColor="white"
              color="primary.400"
              rounded="3xl"
              fontSize={{ base: "xs", sm: "md" }}
              size={{ base: "xs", sm: "md" }}
              textStyle="p"
              disabled={product.variants.length === 0 || product.variants[0].availableQuantity === 0}
              onClick={addToCart}
            >
              ADD TO CART
            </Button>}
            </HStack>
      </VStack>
    </motion.div>
  );
};

export default NewItem;
