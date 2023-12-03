import { VStack, Text, Image, Icon, SimpleGrid, AspectRatio, useBreakpointValue } from '@chakra-ui/react';
import seperator from '../../../static/seperator.png';
import { SiMusicbrainz } from 'react-icons/si';
import { ImHeadphones } from 'react-icons/im';
import { MdForum } from "react-icons/md";
import { IconType } from 'react-icons/lib';
import { motion, TargetAndTransition } from 'framer-motion';
import { GiGlassHeart } from 'react-icons/gi';
import { useTranslation } from 'react-i18next';

export interface OfferProps {

}

const Offer: React.FC<OfferProps> = () => {
    const responsiveSpacing = {sm: '10px', md: '15px', lg: '20px'};
    const {t} = useTranslation();

    return (
    <VStack alignItems="center" spacing={responsiveSpacing} py={responsiveSpacing} backgroundColor="white">
        <Heading />
        <SimpleGrid columns={{sm: 1, md: 2, lg: 4}}>
            <OfferType title={t("aboutPage.offer.first.title")}
            description={t("aboutPage.offer.first.description")}
            icon={SiMusicbrainz}/>
            <OfferType title={t("aboutPage.offer.second.title")}
            description={t("aboutPage.offer.second.description")}
            icon={GiGlassHeart} />
            <OfferType 
            title={t("aboutPage.offer.third.title")}
            description={t("aboutPage.offer.third.description")}
            icon={MdForum} />
            <OfferType
            title={t("aboutPage.offer.fourth.title")}
            description={t("aboutPage.offer.fourth.description")}
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

    const {t} = useTranslation();

    return ( 
    <>
        <Text textStyle="a2"
        color="primary.600" 
        fontWeight="normal"
        textAlign="center"
        userSelect="none" 
        textTransform="uppercase">
            {t("aboutPage.main.title")}
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
        w={{base: '80%', md: '60%', lg: '40%'}}>{t("aboutPage.main.content")}</Text>
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