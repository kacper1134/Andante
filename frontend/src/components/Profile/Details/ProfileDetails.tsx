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
import { useTranslation } from 'react-i18next';

export interface ProfileDetailsProps {
};

const ProfileDetails: React.FC<ProfileDetailsProps> = () => {
    const userDetails: UserDetails | undefined = useSelector((state: RootState) => state.auth.userDetails);
    const userProfile = useUserProfile();
    const [recommended, setRecommended] = useState<ProductCardData[]>([]);
    const [loading, setLoading] = useState(true);

    const [getAllProducts] = useLazyGetAllByIdQuery();

    const {t} = useTranslation();

    const deliveryInformation: Data[] = [
        {label: "profilePage.details.delivery.country.label", value: userDetails?.delivery.country ?? "profilePage.details.delivery.country.placeholder"},
        {label: "profilePage.details.delivery.city.label", value: userDetails?.delivery.city ?? "profilePage.details.delivery.city.placeholder"},
        {label: "profilePage.details.delivery.street.label", value: userDetails?.delivery.street ?? "profilePage.details.delivery.street.placeholder"},
        {label: "profilePage.details.delivery.postalcode.label", value: userDetails?.delivery.postalCode ?? "profilePage.details.delivery.postalcode.placeholder"},
    ];

    const contactInformation: Data[] = [
        {label: "profilePage.details.contact.email.label", value: userDetails?.personal.emailAddress ?? "profilePage.details.contact.email.placeholder"},
        {label: "profilePage.details.contact.phone.label", value: userDetails?.personal.phoneNumber ?? "profilePage.details.contact.phone.placeholder"},
    ];

    const personalDetails: Data[] = [
        {label: "profilePage.details.personal.nickname.label", value: userDetails?.personal.username ?? "profilePage.details.personal.nickname.placeholder"},
        {label: "profilePage.details.personal.gender.label", value: t(userDetails?.personal.gender ?? "profilePage.details.personal.gender.placeholder")},
        {label: "profilePage.details.personal.birthdate.label", value: userDetails?.personal?.dateOfBirth?.toLocaleString(DateTime.DATE_FULL) ?? "profilePage.details.personal.birthdate.placeholder"},
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
        <Text textStyle="h3" color="primary.300" fontSize={headingSize + "px"} textAlign="center" w="100%" bg="purple.50" boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)">{t("profilePage.details.title")}</Text>
        <Card width="100%" px={mainPadding + "px"} py={mainPadding + "px"} mx={mainMargin + "px"} my={mainMargin + "px"} borderRadius={borderRadius}>
            <SimpleGrid columns={columns} spacing="8px" justifyItems="center">
                <AvatarCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} />
                <DataCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} title={t("profilePage.details.personal.title")} properties={personalDetails}/>
                <DataCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} title={t("profilePage.details.contact.title")} properties={contactInformation}/>
                <DataCard borderRadius={borderRadius} fontSize={fontSize} height={cardHeight} width={cardWidth} title={t("profilePage.details.delivery.title")} properties={deliveryInformation}/>
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

    const {t} = useTranslation();

    return <Box position="relative" width={`calc(100% - 2 * ${margin}px)`} p={padding + "px"} m={margin + "px"} borderRadius={borderRadius} boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)" overflowX="hidden">
        <Box ref={dragConstraint} width={`calc(100% + ${overflowWidth}px)`} left={-overflowWidth} position="absolute" />
        <Heading color="primary.800" textStyle="h2" fontSize="32px" position="sticky" left="8px">{t("profilePage.details.recommended.title")}</Heading>
        {loading && <Spinner color="primary.600" size="lg" />}
        {(isEmpty && !loading) && <Center w="100%" mt="10px"><Text fontSize="20px" textStyle="h1">{t("profilePage.details.recommended.nothingContent")}</Text></Center>}
        <HStack animate={controls} as={motion.div} drag={overflowWidth > 0 ? "x" : undefined} dragConstraints={dragConstraint} spacing={itemSpacing + "px"} key={overflowWidth}>{children}</HStack>
    </Box>;
}

export default ProfileDetails;