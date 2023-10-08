import Card from '../../Card/Card';
import { SimpleGrid, useBreakpointValue, Stack, Box, Heading, HStack, Text, Center, Spinner } from '@chakra-ui/react';
import { motion, useAnimationControls } from 'framer-motion';
import AvatarCard from './AvatarCard';
import DataCard, { Data } from './DataCard';
import ProductCard, { ProductCardData } from './ProductCard';
import { useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../../store';
import { UserDetails } from '../../../utils/KeycloakUtils';
import { DateTime } from 'luxon';
import useUserProfile from '../../../hooks/useUserProfile';
import recombeeClient from '../../../config/recombee-config';
import recombee from "recombee-js-api-client";
import { useLazyGetAllByIdQuery } from '../../../store/api/productSlice';

export interface ProfileDetailsProps {
};

const ProfileDetails: React.FC<ProfileDetailsProps> = () => {
    const userDetails: UserDetails | undefined = useSelector((state: RootState) => state.auth.userDetails);
    const userProfile = useUserProfile();
    const [recommended, setRecommended] = useState<ProductCardData[]>([]);
    const [loading, setLoading] = useState(true);

    const [getAllProducts] = useLazyGetAllByIdQuery();

    const deliveryInformation: Data[] = [
        {label: "Country", value: userDetails?.delivery.country ?? "Not provided"},
        {label: "City", value: userDetails?.delivery.city ?? "Not provided"},
        {label: "Street", value: userDetails?.delivery.street ?? "Not provided"},
        {label: "Postal Code", value: userDetails?.delivery.postalCode ?? "Not provided"},
    ];

    const contactInformation: Data[] = [
        {label: "Email Address", value: userDetails?.personal.emailAddress ?? "Not provided"},
        {label: "Phone Number", value: userDetails?.personal.phoneNumber ?? "Not provided"},
    ];

    const personalDetails: Data[] = [
        {label: "Nickname", value: userDetails?.personal.username ?? "Not selected"},
        {label: "Gender", value: userDetails?.personal.gender ?? "Unspecified"},
        {label: "Birthdate", value: userDetails?.personal?.dateOfBirth?.toLocaleString(DateTime.DATE_FULL) ?? "Not provided"},
    ];

    const headingSize = useBreakpointValue({
        base: 24,
        md: 32,
        xl: 36,
    })!;
    
    const fontSize = useBreakpointValue({
        base: 14,
        lg: 15,
        '2xl': 16,
    })!;
    const cardHeight = "150px";
    const cardWidth = useBreakpointValue({
        base: '300px',
        lg: '350px',
        '2xl': '375px',
    })!;
    const productCardWidth = 250;

    const mainPadding = 32;
    const mainMargin = 32;
    const borderRadius = "16px";

    const columns = useBreakpointValue({
        base: 1,
        md: 2,
        '2xl': 3,
    });
    
    useEffect(() => {
        if(userProfile?.username !== undefined) {
            recombeeClient.send(new recombee.RecommendItemsToUser(userProfile?.username, 5, {minRelevance: "low", cascadeCreate: true}), 
            (err: any, recommended: any) => {
                if(!(recommended !== undefined && recommended.recomms.length === 0)) {
                    getAllProducts(recommended.recomms.map((recommendation: {id: number}) => recommendation.id))
                    .then((response: any) => {
                        const recommendations = response.data.map((recommendation: {id:number, name: string, price: number, variants: any[]}) => {return {
                            id: recommendation.id,
                            name: recommendation.name,
                            price: recommendation.price + "$",
                            image: recommendation.variants.length === 0 ? "" : recommendation.variants[0].imageUrl,
                        }});
                        setRecommended(recommendations);
                    })
                    .finally(() => setLoading(false));
                } 
                else {
                    setLoading(false);
                }
            }
            )
        }
    }, [getAllProducts, userProfile?.username]);

    return <>
        <Text textStyle="h3" color="primary.300" fontSize={headingSize + "px"} textAlign="center" w="100%" bg="purple.50" boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)">User Details</Text>
        <Card width="100%" px={mainPadding + "px"} py={mainPadding + "px"} mx={mainMargin + "px"} my={mainMargin + "px"} borderRadius={borderRadius}>
            <SimpleGrid columns={columns} spacing="8px" justifyItems="center">
                <AvatarCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} />
                <DataCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} title="Personal Details" properties={personalDetails}/>
                <DataCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} title="Contact Information" properties={contactInformation}/>
                <DataCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} title="Delivery" properties={deliveryInformation}/>
            </SimpleGrid>   
        </Card>
        <Stack direction={{base: 'column', lg: 'row'}}>
            <Carousel padding={mainPadding} margin={mainMargin} borderRadius={borderRadius} itemWidth={productCardWidth} isEmpty={recommended.length === 0} loading={loading}>
                {recommended.map((item, index) => <ProductCard key={index} data={item} width={productCardWidth + "px"} />)}
            </Carousel>
        </Stack>
    </>
}

export interface CarouselProps {
    padding: number,
    margin: number,
    borderRadius: string,
    itemWidth: number,
    children: JSX.Element[],
    isEmpty: boolean,
    loading: boolean,
};

const Carousel: React.FC<CarouselProps> = ({padding, margin, borderRadius, itemWidth, children, isEmpty, loading}) => {
    const itemSpacing = 8;
    const scrollWidth = children.length * itemWidth + (children.length - 1) * itemSpacing;
    const controls = useAnimationControls();

    const dragConstraint: React.RefObject<HTMLDivElement> = useRef(null);
    
    const [overflowWidth, setOverflowWidth] = useState(0);
    
    const sidebarWidth: number = useSelector((state: RootState) => state.inner.sidebarWidth);
    
    useEffect(() => {
        function resizeHandler() {
            controls.stop();
            const clientWidth = window.innerWidth - sidebarWidth - 2 * margin - 2 * padding;
            const overflowWidth = scrollWidth - clientWidth;
            setOverflowWidth(overflowWidth);
        }
        resizeHandler();
        window.addEventListener("resize", resizeHandler);

        return () => {
            window.removeEventListener("resize", resizeHandler)
        };
    }, [sidebarWidth, dragConstraint, controls, margin, padding, scrollWidth])

    return <Box position="relative" width={`calc(100% - 2 * ${margin}px)`} p={padding + "px"} m={margin + "px"} borderRadius={borderRadius} boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)" overflowX="hidden">
        <Box ref={dragConstraint} width={`calc(100% + ${overflowWidth}px)`} left={-overflowWidth} position="absolute" />
        <Heading color="primary.800" textStyle="h2" fontSize="32px" position="sticky" left="8px">Recommended for You</Heading>
        {loading && <Spinner color="primary.600" size="lg" />}
        {(isEmpty && !loading) && <Center w="100%" mt="10px"><Text fontSize="20px" textStyle="h1">We have nothing to recommend for you right now!</Text></Center>}
        <HStack animate={controls} as={motion.div} drag={overflowWidth > 0 ? "x" : undefined} dragConstraints={dragConstraint} spacing={itemSpacing + "px"} key={overflowWidth}>{children}</HStack>
    </Box>;
}

export default ProfileDetails;