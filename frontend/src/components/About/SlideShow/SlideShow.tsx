import React, { useState, useEffect } from 'react';
import { Text, Image, Flex, Spacer, Box, HStack, VStack, Circle, useBreakpointValue, useToken, Center } from '@chakra-ui/react';
import { ArrowForwardIcon, ArrowBackIcon } from '@chakra-ui/icons';
import { motion, AnimatePresence, useAnimationControls, TargetAndTransition } from 'framer-motion';
import { slideShowVariants } from './variants/slideShowVariants';
import { titleVariants, descriptionVariants } from './variants/slideInfoVariants';
import Utils from '../../../utils/Utils';
import { hexToRGB } from '../../../functions/style-functions';

export type Slide = {
    URL: string,
    content: SlideContent,
};

export type SlideContent = {
    title: string,
    content: string
};

export interface SlideShowProps {
    slides: Slide[],
    displayTime: number,
    filter: string,
    color: string,
}

const SlideShow: React.FC<SlideShowProps> = ({slides, displayTime, filter, color}) => {
    const ref: React.RefObject<HTMLImageElement> = React.createRef();
    
    const slideSx = {
        objectFit: 'cover',
        boxSize: 'inherit',
        filter: filter,
    }

    const [currentSlide, setCurrentSlide] = useState(0);

    useEffect(() => {
        const timer = setTimeout( () => {
            setCurrentSlide((currentSlide + 1) % slides.length);
        }, displayTime);

        return () => clearTimeout(timer);
    }, [currentSlide, displayTime, slides.length]);

    return ( 
    <AnimatePresence>
        {slides.map((slide, index) => index === currentSlide && 
        <Box as={motion.div} 
        variants={slideShowVariants} 
        initial="init" animate="animate" exit="exit" 
        key={index}>
            <Image as={motion.img}  
            sx={slideSx}
            ref={ref} 
            src={slide.URL} />
            <SlideInfo slideContent={slide.content} 
            color={color}/>
        </Box>
        )}
        <NavigationOverlay currentSlide={currentSlide} 
        setCurrentSlide={setCurrentSlide} 
        numberOfSlides={slides.length}
        color={color}/>
    </AnimatePresence>
    );
};

const NavigationOverlay: React.FC<{currentSlide: number, setCurrentSlide: (arg0: number) => (void), numberOfSlides: number, color: string}> = ({ currentSlide, setCurrentSlide, numberOfSlides, color }) => {
    const hoverOffset = useBreakpointValue({sm: 1.5, md: 2.5, xl: 3.5});
    const controls = useAnimationControls();

    const hoverAnimation: (arg0: boolean) => TargetAndTransition = (reverse) => {
        return {
        x: (reverse ? -1 : 1) * hoverOffset!,
        transition: {
                duration: 0.4,
                transition: {
                    yoyo: Infinity,
                }
            }
        }
    }

    useEffect(() => {
        controls.start("selected");
    }
    ,[controls, currentSlide])
    
    const responsiveIconSize = {
        sm: '16px',
        md: '24px',
        lg: '32px',
        xl: '40px',
        '2xl': '48px'
    };

    const responsiveCircleSize = {
        sm: '6px',
        md: '8px',
        lg: '10px',
        xl: '12px',
        '2xl': '14px'
    };


    const iconSx = {
        color: color,
        boxSize: responsiveIconSize,
        cursor: 'pointer',
    };
    
    return  (
        <Flex alignContent="center" >
            <Box as={motion.div} whileHover={hoverAnimation(true)}>
                <ArrowBackIcon 
                    sx={iconSx} 
                    ml="4px"
                    onClick={() => setCurrentSlide(currentSlide === 0 ? numberOfSlides -1 : currentSlide - 1)} />
            </Box>
            <Spacer />
            <HStack alignSelf="end" position="absolute" top="80%">
                {Utils.range(0, numberOfSlides - 1).map((index) => <Circle key={index} as={motion.div} size={responsiveCircleSize}  variants={{selected: { scale: index === currentSlide ? 1.5 : 1}}} animate={controls} bg="latte.50" cursor="pointer" onClick={() => setCurrentSlide(index)}/>)}
            </HStack>
            <Spacer />
            <Box as={motion.div} whileHover={hoverAnimation(false)}>
                <ArrowForwardIcon 
                    sx={iconSx} 
                    mr="4px"
                    onClick={() => setCurrentSlide((currentSlide + 1) % numberOfSlides)} />
            </Box>
        </Flex>
    )
};

const SlideInfo: React.FC<{slideContent: SlideContent, color: string}> = ({slideContent, color}) => {
    const titleFontSize = useBreakpointValue({base: "12px", sm: "14px", md: "16px", lg: "18px", xl: "20px"});
    const contentFontSize = useBreakpointValue({base: "20px", sm: "25px", md: "30px", lg: "40px", xl: "50px"});

    const [primary200] = useToken("colors", ["primary.200"]);
    const backgroundColor = hexToRGB(primary200, 0.4);

    return (
    <VStack position="absolute" bg={`linear-gradient(${backgroundColor},${backgroundColor})`} w="100%" h="100%">
        <VStack as={Center} h="100%">
            <Text as={motion.div}
            fontSize={titleFontSize}
            variants={titleVariants}
            textStyle="p" 
            textTransform="uppercase"
            color={color}
            noOfLines={1}
            >
                {slideContent.title}
            </Text>
            <Text as={motion.div}
            variants={descriptionVariants}
            fontSize={contentFontSize}
            textStyle="p"
            color={color}
            fontStyle='italic'
            w="75vw"
            textAlign="center"
            noOfLines={3}>
                {slideContent.content}
            </Text>
        </VStack>
    </VStack>
    );
};

export default SlideShow;

