import { Flex, HStack, useBreakpointValue } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { useState } from "react";
import Carousel from "../../../Carousel/Carousel";
import { slideOverlayVariants } from "../../../Carousel/AnimationVariants";
import { slideHeight, slideWidth } from "./ProductsCarouselDimensions";
import ProductSlide, { productMargin } from "./ProductSlide";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";

type ProductsCarouselProps = {
  products: ProductOutputDTO[],
  filterMenuWidth: string;
}

const ProductsCarousel = ({products, filterMenuWidth} : ProductsCarouselProps) => {
  const width = parseFloat(useBreakpointValue(slideWidth)!) + 2;
  const totalComponentWidth = productMargin * 2 + width;
  const [isDragOn, setIsDragOn] = useState(false);
  
  return (
    <HStack as={Flex} position="relative" my={5} minHeight={slideHeight} overflowX="hidden">
      <Carousel
        key={totalComponentWidth}
        componentWidth={totalComponentWidth}
        numberOfComponents={products.length}
        margin={2 * productMargin}
        toggleDragOn={setIsDragOn}
        otherComponentsWidth={parseInt(filterMenuWidth)}
      >
        <HStack
          as={motion.div}
          variants={slideOverlayVariants}
          initial="initial"
          animate="animate"
          exit="exit"
        >
      {products.map((product) => (
        <ProductSlide key={product.id} product={product} isDragOn={isDragOn} />
      ))}
      </HStack>
      </Carousel>
    </HStack>
  );
};

export default ProductsCarousel;
