import { VStack, Text, HStack, Button } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import { contentVariants } from "./AnimationVariants";

type SlideTextProps = {
  product: ProductOutputDTO,
};

const SlideText = ({ product }: SlideTextProps) => {
  const navigate = useNavigate();
  const headerSize = {
    base: "24px",
    sm: "28px",
    md: "36px",
    lg: "42px",
    xl: "48px",
  };
  const textSize = {
    base: "20px",
    sm: "24px",
    md: "32px",
    lg: "36px",
    xl: "42px",
  };
  const textColor = "white";
  return (
    <VStack
      as={motion.div}
      variants={contentVariants}
      spacing={8}
      alignItems="flex-start"
    >
      <Text fontSize={headerSize} color={textColor} textStyle="h1">
        Users Choice
      </Text>
      <Text fontSize={textSize} color={textColor} fontWeight="semibold" textStyle="p">
        {product.name}
      </Text>
      <HStack>
        <Text fontSize={textSize} color={textColor} fontWeight="bold" mr="1em" textStyle="p">
          ${product.price}
        </Text>
        <Button rounded="xl" color="primary.700" backgroundColor="white" textStyle="p" onClick={() => navigate(`/shop/product/${product.id}`)}>
          Order Now
        </Button>
      </HStack>
    </VStack>
  );
};

export default SlideText;
