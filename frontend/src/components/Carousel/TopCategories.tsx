import { Flex, HStack, useBreakpointValue } from "@chakra-ui/react";
import { useState } from "react";
import Slide, { ResponsiveStyle, SlideType } from "./Slide";

type TopCategoriesProps = {
  categories: SlideType[];
  isTextVisible: boolean;
  otherComponentsWidth: number;
  slideWidth: ResponsiveStyle;
  slideHeight: ResponsiveStyle;
  slideMargin: ResponsiveStyle;
  carouselHeight: ResponsiveStyle;
  slideTextFontSize: ResponsiveStyle;
};

const TopCategories = ({ categories, slideWidth, slideHeight, slideMargin, carouselHeight, slideTextFontSize, isTextVisible, otherComponentsWidth }: TopCategoriesProps) => {
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
      <Flex
        justifyContent={"center"}
        width={"100%"}
      >
        {categories.slice(0, 4).map((category, index) => (
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
      </Flex>
    </HStack>
  );
};

export default TopCategories;