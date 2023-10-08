import { Box, useBreakpointValue, useToken } from "@chakra-ui/react";
import { AnimatePresence, motion, useAnimationControls } from "framer-motion";
import { useCallback, useEffect, useState } from "react";
import { hexToRGB } from "../../functions/style-functions";
import { backgroundVariants } from "./AnimationVariants";
import SlideOverlay from "./SlideOverlay";
import noimage from "../../static/noimage.png";
import { getDownloadURL, ref } from "firebase/storage";
import storage from "../../config/firebase-config";

export type SlideType = {
  id?: number,
  text: string,
  image: string,
  path: string,
  isExternal?: boolean,
};

export type ResponsiveStyle = {
  base: string;
  sm: string;
  md: string;
  lg: string;
  xl: string;
}

type SlideProps = {
  slide: SlideType;
  isDragOn: boolean;
  isTextVisible: boolean;
  slideWidth: ResponsiveStyle;
  slideHeight: ResponsiveStyle;
  slideMarginX: ResponsiveStyle;
  fontSize: ResponsiveStyle;
};

const Slide = ({ slide, isDragOn, slideWidth, slideHeight, slideMarginX, fontSize, isTextVisible }: SlideProps) => {
  const [primary200] = useToken("colors", ["primary.200"]);
  const [isVisible, setIsVisible] = useState(false);
  const [image, setImage] = useState<string>(noimage);

  const backgroundColor = hexToRGB(primary200, 0.4);

  const controls = useAnimationControls();
  
  const start = useCallback(() => {
    setIsVisible(true);
    controls.start("hoverStart");
  }, [controls]);

  const end = useCallback(() => {
    controls.start("hoverEnd");
    setIsVisible(false);
  }, [controls]);

  useEffect(() => {
    const fetchImage = async () => {
      const downloadUrl = await getDownloadURL(ref(storage, slide.image));

      setImage(downloadUrl);
    }

    fetchImage().catch(() => setImage(noimage));
  }, [slide])

  if (isDragOn && isVisible) {
    end();
  }
  
  const margin = useBreakpointValue(slideMarginX);
  
  return (
    <Box as={motion.div}
      height={slideHeight}
      width={slideWidth}
      animate={controls}
      boxShadow="md"
      rounded="2xl"
      marginX={`${margin}!`}
      overflow="hidden"
      onHoverStart={() => {
        if (!isDragOn) start();
      }}
      onHoverEnd={() => {
        if (!isDragOn) end();
      }}
    >
      <Box
        as={motion.div}
        bgImage={`linear-gradient(${backgroundColor},${backgroundColor}), url(${image})`}
        bgRepeat="no-repeat"
        bgSize="cover"
        bgPosition="center"
        height="full"
        width="full"
        rounded="2xl"
        boxShadow="md"
        cursor="pointer"
        variants={backgroundVariants}
      >
        <AnimatePresence>
          {(isVisible || isTextVisible) && <SlideOverlay slide={slide} fontSize={fontSize} isDragOn={isDragOn} />}
        </AnimatePresence>
      </Box>
    </Box>
  );
};

export default Slide;
