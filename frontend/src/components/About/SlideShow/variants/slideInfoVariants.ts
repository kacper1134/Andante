import { Variants } from "framer-motion";

const iconVariants: Variants = {
    init: {
        y: '40%',
        opacity: 0, 
    },
    animate: {
        y: 0,
        opacity: 1,
        transition: {
            type: 'tween',
            delay: 0.5,
            duration: 0.5,
            ease: 'easeOut',
        }
    },
    exit: {
        y: '-40%',
        opacity: 0,
        transition: {
            type: 'tween',
            duration: 0.5,
            ease: 'easeIn',
        }
    }
}

const titleVariants: Variants = {
    init: {
        y: '40%',
        opacity: 0,
    },
    animate: {
        y: 0,
        opacity: 1,
        transition: {
            type: 'tween',
            delay: 0.8,
            duration: 0.4,
            ease: 'easeOut',
        }
    },
    exit: {
        y: '-40%',
        opacity: 0,
        transition: {
            type: 'tween',
            duration: 0.4,
            ease: 'easeIn',
        }
    }

};

const descriptionVariants: Variants = {
    init: {
        y: '40%',
        opacity: 0,
    },
    animate: {
        y: 0,
        opacity: 1,
        transition: {
            type: 'tween',
            delay: 1.1,
            duration: 0.4,
            ease: 'easeOut',
        }
    },
    exit: {
        y: '-40%',
        opacity: 0,
        transition: {
            type: 'tween',
            duration: 0.4,
            ease: 'easeIn',
        }
    }
};

export { iconVariants, titleVariants, descriptionVariants };