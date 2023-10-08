import { motion } from "framer-motion";
import Slide, { SlideType } from "../../Carousel/Slide";
import {
  topicContentMarginY,
  topicHeight,
  topicWidth,
  slideTextFontSize,
} from "./ForumDimensions";

type TopicProps = {
  topic: SlideType;
};

const Topic = ({ topic }: TopicProps) => {
  return (
    <motion.div initial={{opacity: 0}} animate={{opacity: 1}}>
      <Slide
        slide={topic}
        isDragOn={false}
        slideWidth={topicWidth}
        slideHeight={topicHeight}
        slideMarginX={topicContentMarginY}
        fontSize={slideTextFontSize}
        isTextVisible
      />
    </motion.div>
  );
};

export default Topic;
