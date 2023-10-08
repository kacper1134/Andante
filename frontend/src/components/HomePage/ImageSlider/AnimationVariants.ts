import { Variants } from "framer-motion";

const variants: Variants = {
  init: {
    opacity: 0,
    transition: {
      type: "tween",
      duration: 1.5,
    },
  },
  animate: {
    opacity: 1,
    transition: {
      type: "tween",
      duration: 1.5,
    },
  },
  exit: {
    opacity: 0,
    transition: {
      type: "tween",
      duration: 1.5,
    },
  },
};

const contentVariants: Variants = {
  init: {
    opacity: 0,
    transition: {
      type: "tween",
    },
  },
  animate: {
    opacity: 1,
    transition: {
      delay: 1,
      duration: 0.75,
      ease: "easeOut",
    },
  },
  exit: {
    opacity: 0,
    transition: {
      duration: 0.75,
      ease: "easeIn",
    },
  },
};

export { contentVariants };

export default variants;
