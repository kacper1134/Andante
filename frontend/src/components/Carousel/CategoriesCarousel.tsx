import { useBreakpointValue } from "@chakra-ui/react";
import { useState } from "react";
import Slide, { ResponsiveStyle, SlideType } from "./Slide";
import Carousel from "react-multi-carousel";
import "react-multi-carousel/lib/styles.css";

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

const CategoriesCarousel = ({ categories, slideWidth, slideHeight, slideMargin, slideTextFontSize, isTextVisible }: CategoriesCarouselProps) => {
  const width = parseFloat(slideWidth.lg);
  const margin = parseFloat(useBreakpointValue(slideMargin)!);
  const slideCompleteWidth = width + 2 * margin;
  const [isDragOn, setIsDragOn] = useState(false);

  const responsive = {
    superLargeDesktop: {
      breakpoint: { max: 4000, min: 3000 },
      items: 5
    },
    desktop: {
      breakpoint: { max: 3000, min: 1024 },
      items: 4
    },
    tablet: {
      breakpoint: { max: 1024, min: 464 },
      items: 3
    },
    mobile: {
      breakpoint: { max: 464, min: 0 },
      items: 2
    }
  };
  return (
    <Carousel responsive={responsive}>
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
    </Carousel>
  );
};

export default CategoriesCarousel;
