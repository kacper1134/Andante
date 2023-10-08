import { Variants } from "framer-motion";

export const backgroundVariants: Variants = {
  hoverStart: {
    scale: 1.1,
    transition: {
      duration: 0.7,
      type: "tween",
      ease: "easeIn",
    },
  },
  hoverEnd: {
    scale: 1,
    transition: {
      duration: 0.7,
      type: "tween",
      ease: "easeIn",
    },
  },
};

export const slideOverlayVariants: Variants = {
  initial: {
    opacity: 0,
  },
  animate: {
    opacity: 1,
    transition: { duration: 0.7, type: "tween", ease: "easeIn" },
  },
  exit: {
    opacity: 0,
    transition: { duration: 0.7, type: "tween", ease: "easeIn" },
  },
};
