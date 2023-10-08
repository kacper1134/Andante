import {
  Collapse,
  Text,
  HStack,
  Spacer,
  Box,
  Divider,
  VStack,
} from "@chakra-ui/react";
import { ProductFeatureType } from "./ProductBasicInfo/ProductTypes";

type ProductFeaturesProps = {
  features: ProductFeatureType[];
  isOpen: boolean;
  canAnimate: boolean;
  setAnimationEnd: React.Dispatch<React.SetStateAction<boolean>>;
  width: {
    base: number;
    lg: number;
    xl: number;
    "2xl": number;
  };
};

const fontSize = {
  base: "10px",
  sm: "12px",
  md: "14px",
  lg: "16px",
  xl: "18px",
};

const ProductFeatures = ({ features, isOpen, width, canAnimate, setAnimationEnd }: ProductFeaturesProps) => {
  return (
    <Box
      as={Collapse}
      in={isOpen && canAnimate}
      width={width}
      height="fit-content"
      boxShadow="1px 2px 2px 1px rgba(0,0,0,0.25)"
      bgColor="primary.100"
      px="2%"
      rounded="2xl"
      animateOpacity
      onAnimationComplete={(definion: any) => setAnimationEnd(definion === "exit")}
    >
      {features.map((feature, index) => (
        <VStack key={index}>
          <HStack width="full" pt="1.5%" pb={features.length - 1 > index ? "0" : "1.5%"} fontSize={fontSize}>
            <Text fontWeight="bold" textStyle="p">{feature.name}</Text>
            <Spacer></Spacer>
            <Text width="30%" textStyle="p">{feature.value}</Text>
          </HStack>
          {features.length - 1 > index && <Divider orientation="horizontal" />}
        </VStack>
      ))}
    </Box>
  );
};

export default ProductFeatures;
