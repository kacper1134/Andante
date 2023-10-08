import { Variants } from 'framer-motion';

const profileLinkVariants: (linkWidth: string) => Variants = (linkWidth) => {
    return {
        hidden: {
            width: 0,
            transition: {
                duration: 0,
            }
        },
        visible: {
            width: linkWidth,
            transition: {
                duration: 1,
                type: "tween",
                ease: "easeInOut",
            }
        }
    }
}

const profileContentVariants: Variants = {
    hidden: {
        display: "none",
        opacity: 0,
        transition: {
            duration: 0,
        }
    },
    visible: {
        opacity: 1,
        display: "inline",
        transition: {
            duration: 0.5,
            delay: 1,
        }
    }
};


export { profileLinkVariants, profileContentVariants };