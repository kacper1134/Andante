import { Flex, HStack, useBreakpointValue } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { useState } from "react";
import { slideOverlayVariants } from "./AnimationVariants";
import Carousel from "./Carousel";
import Slide, { ResponsiveStyle, SlideType } from "./Slide";

type CategoriesCarouselProps = {
  categories: SlideType[];
  isTextVisible: boolean;
  otherComponentsWidth: number;
  slideWidth: ResponsiveStyle;
  slideHeight: ResponsiveStyle;
  slideMargin: ResponsiveStyle;
  carouselHeight: ResponsiveStyle;
  slideTextFontSize: ResponsiveStyle;
};

const CategoriesCarousel = ({ categories, slideWidth, slideHeight, slideMargin, carouselHeight, slideTextFontSize, isTextVisible, otherComponentsWidth }: CategoriesCarouselProps) => {
  const width = parseFloat(useBreakpointValue(slideWidth)!);
  const margin = parseFloat(useBreakpointValue(slideMargin)!);
  const slideCompleteWidth = width + 2 * margin;
  const [isDragOn, setIsDragOn] = useState(false);
  
  return (
    <HStack
      as={Flex}
      minHeight={carouselHeight}
      overflowX="hidden"
      position="relative"
      py={10}
    >
      <Carousel
        key={slideCompleteWidth}
        componentWidth={slideCompleteWidth}
        numberOfComponents={categories.length}
        margin={2 * margin}
        toggleDragOn={setIsDragOn}
        otherComponentsWidth={otherComponentsWidth}
      >
        <HStack
          as={motion.div}
          variants={slideOverlayVariants}
          initial="initial"
          animate="animate"
          exit="exit"
        >
          {categories.map((category, index) => (
            <Slide
              key={index}
              fontSize={slideTextFontSize}
              slide={category}
              isDragOn={isDragOn}
              slideHeight={slideHeight}
              slideWidth={slideWidth}
              slideMarginX={slideMargin}
              isTextVisible={isTextVisible}
            />
          ))}
        </HStack>
      </Carousel>
    </HStack>
  );
};

export default CategoriesCarousel;