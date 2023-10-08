import { Variants } from 'framer-motion';

const profileNavigationVariants: (sidebarWidth: string) => Variants = (sidebarWidth) => {
    return {
        initial: {
            width: 0,
            transition: {
                type: "tween",
                duration: 1,
            }
        },
        animate: {
            width: sidebarWidth,
            transition: {
                type: "tween",
                duration: 1,
            }
        },
        exit: {
            width: 0,
            transition: {
                type: "tween",
                delay: 0.5,
                duration: 1,
            }
        }
    }
};

const profileContentVariants: Variants = {
    initial: {
        opacity: 0,
        transition: {
            type: "tween",
            duration: 1,
        }
    },
    animate: {
        opacity: 1,
        transition: {
            type: "tween",
            delay: 0.75,
            duration: 1,
        }
    },
    exit: {
        opacity: 0,
        transition: {
            type: "tween",
            duration: 1,
        }
    }
}

export { profileNavigationVariants, profileContentVariants };