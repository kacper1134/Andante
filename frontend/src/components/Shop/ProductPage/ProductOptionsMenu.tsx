import {
  Text,
  Button,
  Flex,
  HStack,
  Spacer,
  useBreakpointValue,
  VStack,
  Icon,
} from "@chakra-ui/react";
import { useState } from "react";
import { BsTools } from "react-icons/bs";
import { FaComments } from "react-icons/fa";
import { ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import { imageHeight } from "./ProductBasicInfo/ProductDimensions";
import ProductComments from "./ProductComments/ProductComments";
import ProductFeatures from "./ProductFeatures";
import { extractFeatures } from "../../../utils/Utils";

type ProductOptionsMenuProps = {
  product: ProductOutputDTO,
  selectedVariantId: number,
};

const buttonFontSize = {
  base: "12px",
  sm: "14px",
  md: "16px",
  lg: "18px",
  xl: "20px",
  "2xl": "24px",
};

const ProductMenuHeight = {
  base: "25px",
  sm: "30px",
  md: "35px",
  lg: "40px",
  xl: "45px",
  "2xl": "50px",
};

const ProductOptionsMenu: React.FC<ProductOptionsMenuProps> = ({product, selectedVariantId}) => {
  const imageWidth = parseInt(useBreakpointValue(imageHeight)!) * 1.5;
  const width = {
    base: imageWidth,
    lg: 320 + imageWidth,
    xl: 440 + imageWidth,
    "2xl": 560 + imageWidth,
  };

  const [areFeaturesOpen, setAreFeaturesOpen] = useState(true);
  const [featuresAnimationEnd, setFeaturesAnimationEnd] = useState(false);
  const [commentsAnimationEnd, setCommentsAnimationEnd] = useState(true);

  return (
    <>
      <VStack width={width} height={ProductMenuHeight}>
        <Flex w="100%" direction="column">
          <Flex w="100%">
            <Button
              w="48%"
              fontWeight="bold"
              fontSize={buttonFontSize}
              colorScheme="primary"
              height="fit-content"
              py="0.75%"
              opacity={areFeaturesOpen ? "1" : "0.5"}
              onClick={() => setAreFeaturesOpen(true)}
            >
              <HStack>
                <Icon as={BsTools} />
                <Text textStyle="h1">Features</Text>
              </HStack>
            </Button>
            <Spacer />
            <Button
              w="48%"
              fontWeight="bold"
              fontSize={buttonFontSize}
              colorScheme="primary"
              height="fit-content"
              py="0.75%"
              opacity={areFeaturesOpen ? "0.5" : "1"}
              onClick={() => setAreFeaturesOpen(false)}
            >
              <HStack>
                <Icon as={FaComments} />
                <Text textStyle="h1">Comments</Text>
              </HStack>
            </Button>
          </Flex>
        </Flex>
      </VStack>
      <ProductFeatures
        width={width}
        isOpen={areFeaturesOpen}
        features={extractFeatures(product, selectedVariantId)}
        canAnimate={commentsAnimationEnd}
        setAnimationEnd={setFeaturesAnimationEnd}
      />
      <ProductComments
        width={width}
        isOpen={!areFeaturesOpen}
        product={product}
        canAnimate={featuresAnimationEnd}
        setAnimationEnd={setCommentsAnimationEnd}
      />
    </>
  );
};

export default ProductOptionsMenu;
