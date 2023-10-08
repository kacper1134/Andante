import { Box, VStack, useToken, HStack, Spacer } from "@chakra-ui/react";
import { hexToRGB } from "../../../functions/style-functions";
import { ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import SlideText from "./SlideText";

export interface SlideProps {
  product: ProductOutputDTO,
  image: string;
}

const Slide = ({ product, image }: SlideProps) => {
  const [primary200] = useToken("colors", ["primary.200"]);
  const backgroundColor = hexToRGB(primary200, 0.4);
  
  return (
    <Box
      bgImage={`linear-gradient(${backgroundColor},${backgroundColor}), url(${image})`}
      bgRepeat="no-repeat"
      bgSize="cover"
      bgPosition="center"
      w="100%"
    >
      <HStack height="100%">
        <VStack spacing="8" pl={{ lg: "5em", md: "3em", base: "1em" }}></VStack>
        <Spacer />
        <VStack>
          <SlideText
            product={product}
          />
        </VStack>
        <Spacer />
      </HStack>
    </Box>
  );
};

export default Slide;
