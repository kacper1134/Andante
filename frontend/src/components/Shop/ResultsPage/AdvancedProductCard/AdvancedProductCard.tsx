import { Image, Flex, Text, HStack, Icon, useToken, Spacer } from '@chakra-ui/react';
import { motion } from "framer-motion";
import { AdvancedProductCardVariants, CommentsVariants } from './Variants';
import { Rating } from 'react-simple-star-rating';
import { GoComment } from 'react-icons/go';
import { ProductOutputDTO } from '../../../../store/api/result/dto/product/base/ProductOutputDTO';
import noimage from "../../../../static/noimage.png";
import { useFirebase } from '../../../../hooks/useFirebase';
import { useNavigate } from 'react-router-dom';

export interface SaleDetails {
    previousPrice: number,
    remainingDays: number,
}

export interface AdvancedProductCardProps {
    data: ProductOutputDTO,
}

function roundToThousands(value: number): string {
    if (value < 1000) {
        return "" + value;
    }

    return Math.round(value / 100) / 10 + "K";
}


const AdvancedProductCard: React.FC<AdvancedProductCardProps> = ({data}) => {
    const navigate = useNavigate();
    const starColor = useToken("colors", "secondary.500");
    const producerImage = useFirebase(data.producer.imageUrl, noimage);
    const productImage = useFirebase(data.variants.length === 0 ? noimage : data.variants[0].thumbnailUrl, noimage);

    return <Flex position="relative" flexDirection="column" whileHover="hover" cursor="pointer" as={motion.div} variants={AdvancedProductCardVariants} direction="column" borderRadius="16px" p="16px" bg="gray.100" mr="16px" mb="16px"
    onClick={() => navigate(`/shop/product/${data.id}`)}>
        <Image src={producerImage} boxSize="24px" objectFit="contain" position="absolute" left="2px" top="2px" />
        <Image src={productImage} boxSize="250px" objectFit="contain" />
        <Text color="primary.400" textStyle="h1" noOfLines={2} w="225px" alignSelf="center" fontSize="16px">{data.name}</Text>
        <HStack w="225px" h="fit-content" alignSelf="center" pt="8px" fontSize="18px">
            <Text textStyle="h3" color="black" paddingInlineEnd="8px">${data.price}</Text>
        </HStack>
        <HStack w="225px" h="fit-content" alignSelf="center" pt="4px">
            <Rating initialValue={data.averageRating} fillColor={starColor} size={12} SVGstorkeWidth={1} allowFraction readonly />
            <Text textStyle="h3" color="black" fontSize="12px">{roundToThousands(data.comments.length)}</Text>
        </HStack>
        <HStack w="225px" h="fit-content" alignSelf="center" pt="4px">
            <HStack as={motion.div} variants={CommentsVariants} whileHover="hover" alignSelf="end">
                <Icon as={GoComment} boxSize="14px" mb="2px" alignSelf="end" />
                <Text alignSelf="end" textStyle="h3" fontSize="14px" color="black">{roundToThousands(data.comments.length)}</Text>
            </HStack>
            <Spacer />
        </HStack>
    </Flex>
}


export default AdvancedProductCard;