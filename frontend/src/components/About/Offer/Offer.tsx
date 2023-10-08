import { VStack, Text, Image, Icon, SimpleGrid, AspectRatio, useBreakpointValue } from '@chakra-ui/react';
import seperator from '../../../static/seperator.png';
import { SiMusicbrainz } from 'react-icons/si';
import { ImHeadphones } from 'react-icons/im';
import { MdForum } from "react-icons/md";
import { IconType } from 'react-icons/lib';
import { motion, TargetAndTransition } from 'framer-motion';
import { GiGlassHeart } from 'react-icons/gi';

export interface OfferProps {

}

const Offer: React.FC<OfferProps> = () => {
    const responsiveSpacing = {sm: '10px', md: '15px', lg: '20px'};

    return (
    <VStack alignItems="center" spacing={responsiveSpacing} py={responsiveSpacing} backgroundColor="white">
        <Heading />
        <SimpleGrid columns={{sm: 1, md: 2, lg: 4}}>
            <OfferType title="All about music"
            description="The andante portal is aimed at music fans and is all about music"
            icon={SiMusicbrainz}/>
            <OfferType title="Made with love"
            description="Our team believes that if we create our product with passion then users can feel it."
            icon={GiGlassHeart} />
            <OfferType 
            title="Talk about your passion"
            description="Andante provides opportunity to discuss music with other music fans"
            icon={MdForum} />
            <OfferType
            title="Amazing devices"
            description="Pamper yourself with our high quality music devices"
            icon={ImHeadphones} />
            </SimpleGrid>
    </VStack>
    );
};

const Heading: React.FC<{}> = () => {
    const seperatorSize = {
        base: '100px',
        md: '125px',
        lg: '150px',
        xl: '175px',
        '2xl': '200px',
    }

    return ( 
    <>
        <Text textStyle="a2"
        color="primary.600" 
        fontWeight="normal"
        textAlign="center"
        userSelect="none" 
        textTransform="uppercase">
            about us
        </Text>
        <AspectRatio ratio={3} w={seperatorSize}>
            <Image src={seperator}
            userSelect="none" 
            objectFit="scale-down"
            />
        </AspectRatio>
        <Text
        mt="10px"
        textStyle="s2"
        userSelect="none"
        textAlign="center"
        w={{base: '80%', md: '60%', lg: '40%'}}>The culture of our company, which is based on openness and orientation to employees and customers, makes cooperation with us effective, we achieve goals faster and we overcome difficulties much more efficiently. We are choice in the digital transformation of business and affects the level of customer satisfaction.</Text>
    </>
    );
};

interface OfferTypeProps {
    title: React.ReactNode,
    description: React.ReactNode,
    icon: IconType,
};

const OfferType: React.FC<OfferTypeProps> = ({title, description, icon}) => {
    const hoverEffect: TargetAndTransition = {
        scale: [1, 1.05],
        transition: {
            scale: {
                repeat: Infinity,
                repeatType: 'reverse'
            },
        },
    };

    const iconHeaderFontSize = useBreakpointValue({base: "16px", sm: "18px", md: "20px", lg: "22px", xl: "26px"});
    const iconSize = useBreakpointValue({base: "50px", sm: "60px", md: "70px", lg: "80px", xl: "90px"});

    return (
    <VStack alignItems="center" userSelect="none" mt="30px">
        <Icon as={icon}
        color="primary.500"
        objectFit="cover"
        boxSize={iconSize} />
        <Text 
        as={motion.div}
        whileHover={hoverEffect}
        textStyle="a1"
        fontSize={iconHeaderFontSize}
        textAlign="center"
        cursor="pointer"
        textTransform="uppercase">
            {title}
        </Text>
        <Text textStyle="s2"
        textAlign="center"
        w={{sm: '80vw', md: '40vw', lg: '20vw'}}
        >
            {description}
        </Text>
    </VStack>
    );
};

export default Offer;