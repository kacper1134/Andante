const slideShowVariants = {
    init: {
        opacity: 0,
        transition: {
            type: 'tween',
            duration: 1.5,
        },
    },
    animate: {
        opacity: 1,
        transition: {
            type: 'tween',
            duration: 1.5,
        },
    },
    exit: {
        opacity: 0,
        transition: {
            type: 'tween',
            duration: 1.5,
        },
    }
};

export { slideShowVariants };