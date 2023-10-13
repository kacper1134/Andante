import { VStack, Breadcrumb, BreadcrumbItem, BreadcrumbLink, Icon, Divider, Text, HStack, useToken, Box, Spacer, Popover, PopoverTrigger, PopoverContent, PopoverCloseButton, PopoverHeader, PopoverBody, IconButton } from "@chakra-ui/react";
import { BsDot } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import { Rating } from "react-simple-star-rating";
import { FaComment, FaFacebook, FaTumblr } from 'react-icons/fa';
import { HiOutlineDotsVertical } from "react-icons/hi";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import { useFirebase } from "../../../../hooks/useFirebase";
import noimage from "../../../../static/noimage.png";
import { FacebookShareButton, LinkedinShareButton, TumblrShareButton, TwitterShareButton } from "react-share";
import { GrTwitter } from "react-icons/gr";
import { AiFillLinkedin } from "react-icons/ai";

export interface Link {
    text: string,
    to: string,
};

export interface ProductCardProps {
    data: ProductOutputDTO,
}

function roundToThousands(value: number): string {
    if (value < 1000) {
        return "" + value;
    }

    return Math.round(value / 100) / 10 + "K";
}

const ProductCard: React.FC<ProductCardProps> = ({data}) => {
    const navigate = useNavigate();
    const fillColor = useToken("colors", "gray.400");
    const iconSize = "32px";
    const productImage = useFirebase(data.variants.length > 0 ? data.variants[0].imageUrl : noimage, noimage);
    const productUrl = "http://" + window.location.host + `/shop/product/${data.id}`;


    const breadcrumbs: Link[] = [
        { text: "Shop", to: "/shop"},
        { text: "Product", to: "/shop/filtered"},
        { text: `${data.id}`, to: `/shop/product/${data.id}`}
    ];

    const width = {
        base: "300px",
        sm: "400px",
        md: "450px"
    }

    return <VStack w={width} borderRadius="8px" boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)" px="16px" spacing={0} bgColor="white">
        <Box w={width} h="200px" backgroundImage={productImage} backgroundPosition="center" backgroundRepeat="no-repeat" backgroundSize="fill">
            <Box backgroundColor="rgba(0,0,0,0.1)" w="inherit" h="inherit" />
        </Box>
        <Divider borderColor="purple.400" w={width} />
        <ProductBreadcrumbs breadcrumbs={breadcrumbs} />
        <Text textStyle="p" alignSelf="start" noOfLines={2} 
        fontSize="20px" color="primary.400" userSelect="none" 
        cursor="pointer" onClick={() => navigate(breadcrumbs[breadcrumbs.length - 1].to)}>{data.name}</Text>
        <HStack alignSelf="start">
            <Box as={Rating} initialValue={data.averageRating} fillColor={fillColor} size={14} mb="4px" allowFraction readonly/>
            <Text textStyle="p" fontSize="14px">{roundToThousands(data.comments.length)}</Text>
        </HStack>
        <Divider borderColor="purple.400" w={width} />
        <HStack alignSelf="start" color={fillColor} py="8px" pe="16px" w="inherit">
            <Icon as={FaComment} boxSize="14px" alignSelf="center" />
            <Text color="black" textStyle="p" fontSize="14px">{roundToThousands(data.comments.length)}</Text>
            <Spacer />
            <Popover>
                <PopoverTrigger>
                    <IconButton icon={<Icon as={HiOutlineDotsVertical} boxSize="20px" />} aria-label="Social" bgColor="white" _hover={{}} _active={{}} />
                </PopoverTrigger>
                <PopoverContent>
                    <PopoverCloseButton boxSize="28px" />
                    <PopoverHeader fontSize="20px">SHARE</PopoverHeader>
                    <PopoverBody>
                        <HStack justifyContent="space-around">
                            <TwitterShareButton children={<Icon as={GrTwitter} color="twitter.500" boxSize={iconSize} />} url={productUrl} />
                            <FacebookShareButton children={<Icon as={FaFacebook} color="facebook.500" boxSize={iconSize}/>} url={productUrl} hashtag="andante"/>
                            <TumblrShareButton children={<Icon as={FaTumblr} color="#34526F" boxSize={iconSize} />} url={productUrl} />
                            <LinkedinShareButton children={<Icon as={AiFillLinkedin} color="#0E76A8" boxSize={iconSize} />} url={productUrl} />
                        </HStack>
                    </PopoverBody>
                </PopoverContent>
            </Popover>
        </HStack>
    </VStack>
}

interface ProductBreadcrumbsProps {
    breadcrumbs: Link[],
} 

const ProductBreadcrumbs: React.FC<ProductBreadcrumbsProps> = ({breadcrumbs}) => {
    return (
        <Breadcrumb separator={<Icon as={BsDot} color="primary.400" />} alignSelf="start" py="4px">
            {breadcrumbs.map((link, index) => 
                <BreadcrumbItem key={index}>
                    <BreadcrumbLink href={link.to} color="primary.400" _hover={{}}>{link.text}</BreadcrumbLink>
                </BreadcrumbItem>)}
        </Breadcrumb>
    )
}

export default ProductCard;