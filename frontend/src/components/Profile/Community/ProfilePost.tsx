import { VStack, HStack, Spacer, Text, Icon, Box } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { AiFillLike } from "react-icons/ai";
import { motion } from "framer-motion";
import { CommentOutputDTO } from "../../../store/api/result/dto/product/CommentOutputDTO";
import parse from "html-react-parser";
import { DateTime } from "luxon";

export interface Link {
    link: string,
    text: string,
};

export interface ProfilePostProps {
    comment: CommentOutputDTO,
}

function roundToThousands(value: number): string {
    if (value < 1000) {
        return "" + value;
    }

    return Math.round(value / 100) / 10 + "K";
}

const ProfilePost: React.FC<ProfilePostProps> = ({comment}) => {
    const navigate = useNavigate();

    const width = {
        base: "300px",
        sm: "400px",
        md: "450px"
    }

    return ( 
    <VStack w={width} bg="white" borderRadius="16px" boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)" py="4px" px="8px" maxW="1000px" alignSelf="start">
        <HStack w="100%">
            <Spacer />
            <Text color="primary.400" userSelect="none">{DateTime.fromISO(comment.creationTimestamp).toLocaleString(DateTime.DATETIME_MED)}</Text>
        </HStack>
        <Text textStyle="p" fontWeight={900} color="primary.400" fontSize="20px" noOfLines={2} userSelect="none" cursor="pointer" alignSelf="start" maxW="700px" onClick={() => navigate(`/shop/product/${comment.productId}`)}>{comment.title}</Text>
        <Box textStyle="p" fontWeight={400} color="gray.700" fontSize="16px" noOfLines={8} alignSelf="start" maxW="1000px">{parse(comment.content)}</Box>
        <HStack as={motion.div} whileHover={{y: "-5px", transition: { repeat: Infinity, repeatType: "reverse", duration: 1}}} alignSelf="end" color="primary.400">
            <Icon as={AiFillLike} boxSize="16px" mb="2px" />
            <Text fontSize="16px">{roundToThousands(comment.observers.length)}</Text>
        </HStack>
    </VStack>
    );
}

export interface ProfileBreadcrumbProps {
    linkHierarchy: Link[],
}

export default ProfilePost;