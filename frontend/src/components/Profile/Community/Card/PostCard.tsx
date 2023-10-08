import { Text, HStack, Breadcrumb, BreadcrumbLink, BreadcrumbItem, Icon, VStack, Spacer, Tooltip, Box  } from "@chakra-ui/react";
import { BsDot, BsReplyFill, BsFillArrowUpCircleFill } from "react-icons/bs";
import { PostOutputDTO } from "../../../../store/api/result/dto/forum/PostOutputDTO";
import parser from "html-react-parser";

export interface Link {
    to: string,
    text: string,
}

export interface Post {
    title: string,
    content: string,
}

export interface PostCardData {
    linkHierarchy: Link[],
    post: Post,
    repliesCount: number,
    upvotesCount: number,
    participatorsCount: number,
    image: string,
};

export interface PostCardProps {
    data: PostOutputDTO,
};

function roundToThousands(value: number): string {
    if (value < 1000) {
        return "" + value;
    }

    return Math.round(value / 100) / 10 + "K";
}

const PostCard: React.FC<PostCardProps> = ({data}) => {
    const linkHierarchy: Link[] = [
        {to: "/forum", text: "Forum"},
        {to: `/forum/post/${data.id}`, text: "" + data.id}
    ]

    return <HStack h="275px" spacing={0}>
        <VStack h="inherit" bg="white" px="4px" w="300px" borderEndRadius="16px" spacing={0}>
            <PostBreadcrumbs linkHierarchy={linkHierarchy} />
            <Text color="primary.400" textStyle="h3" fontSize="22px" alignSelf="start">{data.title}</Text>
            <Box color="gray.500" textStyle="p" fontSize="16px" alignSelf="start" wordBreak="break-all" noOfLines={6}>{parser(data.content)}</Box>
            <Spacer />
            <HStack justifyContent="space-around" w="inherit">
                <Tooltip label="Replies" bg="primary.400">
                    <VStack spacing="8px" color="gray.400">
                        <Icon as={BsReplyFill} boxSize="20px" />
                        <Text fontSize="20px">{roundToThousands(data.responsesAmount)}</Text>
                    </VStack>
                </Tooltip>
                <Tooltip label="Upvotes" bg="primary.400">
                    <VStack spacing="8px" color="gray.400">
                        <Icon as={BsFillArrowUpCircleFill} boxSize="20px" />
                        <Text fontSize="20px">{roundToThousands(data.likesAmount)}</Text>
                    </VStack>
                </Tooltip>
            </HStack>
        </VStack>
    </HStack>
}

interface PostBreadcrumbs {
    linkHierarchy: Link[],
}

const PostBreadcrumbs: React.FC<PostBreadcrumbs> = ({linkHierarchy}) => {
    return (
        <Breadcrumb separator={<Icon as={BsDot} color="primary.400" />} alignSelf="start" py="4px">
        {linkHierarchy.map((link, index) => 
        <BreadcrumbItem key={index}>
            <BreadcrumbLink href={link.to} color="primary.400" fontSize="14px" _hover={{}}>{link.text}</BreadcrumbLink>
        </BreadcrumbItem>)}
        </Breadcrumb>
    )
}

export default PostCard;