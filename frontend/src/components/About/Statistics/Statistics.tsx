import React from 'react';
import { AspectRatio, Image, Box, SimpleGrid, VStack, Text, useBreakpointValue } from "@chakra-ui/react";
import useVerticalViewportOffset from '../../../hooks/useVerticalViewportOffset';
import { useTranslation } from 'react-i18next';

export interface StatisticsProps {

}

const GRID_ITEMS: StatisticItemProps[] = [
    {
        count: 250,
        caption: 'aboutPage.statistics.first.caption',
        description: 'aboutPage.statistics.first.description',
    }, 
    {
        count: 22,
        caption: 'aboutPage.statistics.first.caption',
        description: 'aboutPage.statistics.first.description',
    },
    {
        count: 4,
        caption: 'aboutPage.statistics.first.caption',
        description: 'aboutPage.statistics.first.description',
    },
    {
        count: 41,
        caption: 'aboutPage.statistics.first.caption',
        description: 'aboutPage.statistics.first.description',
    }
];

const Statistics: React.FC<StatisticsProps> = () => {
    const aspectRatio = {base: 8/3, md: 32/9};
    const ref: React.RefObject<HTMLDivElement> = React.createRef();
    const offset = useVerticalViewportOffset(ref, {from: -15, to: 15});
    const {t} = useTranslation();
    return (
        <>
            <AspectRatio ratio={aspectRatio} mt="10px">
                <Box
                ref={ref}
                overflow="hidden">
                    <Image
                    src="https://images.unsplash.com/photo-1554668048-5055c5654bbc?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80"
                    position="relative"
                    top={`${offset}%`}
                    transform="scale(1.5)"
                    objectFit="cover" />
                </Box>
            </AspectRatio>
            <SimpleGrid columns={{base: 1, md: 2, lg: 4}} spacing={{md: 10}} w="90%" alignSelf="center">
                {GRID_ITEMS.map(item => <GridText key={item.caption} count={item.count} caption={t(item.caption)} description={t(item.description)} />)}
            </SimpleGrid>
        </>
    );
};

interface StatisticItemProps {
    count: number,
    caption: string,
    description: string,
};

const GridText: React.FC<StatisticItemProps> = ({count, caption, description}) => {
    const countFontSize = useBreakpointValue({base: "30px", sm: "35px", md: "40px", lg: "45px", xl: "50px"})
    return (
        <VStack py="20px" 
        textAlign="center"
        userSelect="none">
            <Text fontSize={countFontSize} textStyle="s" color="#9575CD">{count}</Text>
            <Text textStyle="p2" textTransform="uppercase">{caption}</Text>
            <Text textStyle="s2" maxW="50vw">{description}</Text>
        </VStack>
    )
}

export default Statistics;