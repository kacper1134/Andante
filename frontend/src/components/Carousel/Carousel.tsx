import { Box } from "@chakra-ui/react";
import { AnimatePresence, motion } from "framer-motion";
import { useEffect, useRef, useState } from "react";

type CarouselProps = {
  children: React.ReactNode;
  componentWidth: number;
  numberOfComponents: number;
  margin: number;
  otherComponentsWidth: number;
  toggleDragOn: React.Dispatch<React.SetStateAction<boolean>>;
};

const debouncingValue = 15;

const SlideMoveAnimation = ({
  children,
  componentWidth,
  numberOfComponents,
  margin,
  otherComponentsWidth: leftComponentsWidth,
  toggleDragOn,
}: CarouselProps) => {
  const constraintsRef = useRef(null);
  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  
  useEffect(() => {
    const handleResize = () => {
      if (Math.abs(window.innerWidth - windowWidth) >= debouncingValue)
        setWindowWidth(window.innerWidth);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [windowWidth]);

  const freeWidth = windowWidth - leftComponentsWidth;
  const numberOfHiddenComponents = numberOfComponents - freeWidth / componentWidth;
  const moveZoneWidth = numberOfHiddenComponents * componentWidth + margin;
  const carouselWidth = freeWidth + 2 * moveZoneWidth;
  const leftOffset = -moveZoneWidth;
  
  return (
    <>
      <Box
        width={carouselWidth}
        height="full"
        position="absolute"
        left={leftOffset}
        ref={constraintsRef}
      />
      <Box
        as={motion.div}
        drag={numberOfHiddenComponents > 0 ? "x" : undefined}
        dragTransition={{ bounceStiffness: 50, bounceDamping: 15 }}
        dragElastic={0}
        dragConstraints={constraintsRef}
        position="absolute"
        onDragStart={() => toggleDragOn(true)}
        onDragTransitionEnd={() => toggleDragOn(false)}
      >
        <AnimatePresence>{children}</AnimatePresence>
      </Box>
    </>
  );
};

export default SlideMoveAnimation;
