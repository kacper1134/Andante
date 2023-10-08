import CategoriesCarousel from "../../Carousel/CategoriesCarousel";
import { SlideType } from "../../Carousel/Slide";
import {
  slideTextFontSize,
  topicCarouselHeight,
  topicCarouselMarginX,
  topicHeight,
  topicWidth,
} from "./ForumDimensions";

type TopicsCarouselProps = {
  topics: SlideType[];
  otherComponentsWidth: number;
};

const TopicsCarousel = ({ topics, otherComponentsWidth }: TopicsCarouselProps) => {
  return (
    <CategoriesCarousel
      categories={topics}
      slideWidth={topicWidth}
      slideHeight={topicHeight}
      slideMargin={topicCarouselMarginX}
      carouselHeight={topicCarouselHeight}
      slideTextFontSize={slideTextFontSize}
      otherComponentsWidth={otherComponentsWidth}
      isTextVisible
    />
  );
};

export default TopicsCarousel;
