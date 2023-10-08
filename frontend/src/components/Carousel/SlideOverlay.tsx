import { Box, Center, Text } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { hexToRGB } from "../../functions/style-functions";
import { slideOverlayVariants } from "./AnimationVariants";
import { ResponsiveStyle, SlideType } from "./Slide";

type SlideOverlayProps = {
  slide: SlideType;
  fontSize: ResponsiveStyle;
  isDragOn: boolean;
};

const SlideOverlay = ({ slide, fontSize, isDragOn }: SlideOverlayProps) => {
  const backgroundColor = hexToRGB("#000000", 0.4);
  const navigate = useNavigate();
  const [canNavigate, setCanNavigate] = useState(true);
  
  return (
    <Box
      as={motion.div}
      variants={slideOverlayVariants}
      initial="initial"
      animate="animate"
      exit="exit"
      width="full"
      height="full"
      background={`linear-gradient(${backgroundColor},${backgroundColor})`}
      rounded="2xl"
      onAnimationStart={(definition: any) => {
        if (definition === "exit") setCanNavigate(false);
        else setCanNavigate(true);
      }}
      onClick={() => {
        if (canNavigate && !isDragOn) { 
          if (!slide.isExternal) {
              navigate(slide.path + "/" + slide.id);
          } else {
            window.open(slide.path);
          }
        }
      }}
    >
      <Center height="full">
        <Text color="primary.50" fontWeight="semibold" fontSize={fontSize} textStyle='p'>
          {slide.text}
        </Text>
      </Center>
    </Box>
  );
};

export default SlideOverlay;
