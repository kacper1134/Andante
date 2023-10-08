import { Tabs, Text, VStack, TabList, Tab, HStack, useToken, TabPanels, TabPanel, Wrap, useToast } from "@chakra-ui/react";
import ProductCard from "./Card/ProductCard";
import PostCard from "./Card/PostCard";
import { useEffect, useState } from "react";
import { isProductOutputDTOArray, ProductOutputDTO } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import { useGetObservedCommentsQuery, useGetObservedQuery } from "../../../store/api/productSlice";
import { CommentOutputDTO, isCommentOutputDTOArray } from "../../../store/api/result/dto/product/CommentOutputDTO";
import ProfilePost from "./ProfilePost";
import { UserDetails } from "../../../utils/KeycloakUtils";
import { useGetLikedPostsQuery } from "../../../store/api/forum-api-slice";
import { PostOutputDTO } from "../../../store/api/result/dto/forum/PostOutputDTO";

export interface ObservedProps {
    userDetails: UserDetails
}

export enum ObservedCategory {
    PRODUCTS,
    POSTS,
    COMMENTS,
};

const Observed: React.FC<ObservedProps> = ({ userDetails }) => {
    const primary400 = useToken("colors", "primary.400");
    const [observedProducts, setObservedProducts] = useState<ProductOutputDTO[]>([]);
    const [observedComments, setObservedComments] = useState<CommentOutputDTO[]>([]);
    const [observedPosts, setObservedPosts] = useState<PostOutputDTO[]>([]);
    const { data: productsData } = useGetObservedQuery(userDetails!.personal.emailAddress, {refetchOnMountOrArgChange: true});
    const { data: commentsData } = useGetObservedCommentsQuery(userDetails!.personal.emailAddress, {refetchOnMountOrArgChange: true});
    const { data: postData } = useGetLikedPostsQuery(userDetails!.personal.emailAddress, {refetchOnMountOrArgChange: true});
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const toast = useToast();

    useEffect(() => {
        if (productsData) {
            if (isProductOutputDTOArray(productsData)) {
                setObservedProducts(productsData);
            } else {
                setErrorMessages((currentErrors) => [...currentErrors, ...productsData]);
            }
        }
    }, [productsData]);

    useEffect(() => {
        if (commentsData) {
            if (isCommentOutputDTOArray(commentsData)) {
                setObservedComments(commentsData);
            } else {
                setErrorMessages((currentErrors) =>  [...currentErrors, ...commentsData]);
            }
        }
    }, [commentsData]);

    useEffect(() => {
        if (postData) {
            setObservedPosts(postData);
        }
    }, [postData]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: "Something went wrong",
            description: message,
            status: "error",
            duration: 9000,
            isClosable: true,
        }))
    }, [errorMessages, toast]);

    const filterFontSize = {
        base: "10px",
        sm: "16px",
    }

    return <VStack w="100%" px="16px" py="8px" bg="purple.50">
        <Text color="primary.400" textStyle="h3" fontSize="16px" userSelect="none" alignSelf="start">OBSERVED</Text>
        <Tabs variant="solid-rounded" alignSelf="start">
            <HStack>
                <Text color="gray.400" fontSize="18px" textStyle="p" fontWeight={800}>Filter</Text>
                <TabList mx="16px">
                    {Object.values(ObservedCategory).filter(category => isNaN(Number(category))).map((category, index) => <Tab key={index} border={`1px solid ${primary400}`} color="gray.500" _selected={{bg: "primary.400", color: "white"}} mx="6px" fontSize={filterFontSize}>{category}</Tab>)}
                </TabList>
            </HStack>
            <TabPanels>
                <TabPanel>
                    <Wrap spacing="16px">
                        {observedProducts.map((product, index) => <ProductCard key={index} data={product} />)}
                    </Wrap>
                </TabPanel>
                <TabPanel>
                    <Wrap spacing="16px">
                        {observedPosts.map((post, index) => <PostCard key={index} data={post} />)}
                    </Wrap>
                </TabPanel>
                <TabPanel>
                    <Wrap spacing="16px">
                        {observedComments.map((comment, index) => <ProfilePost key={index} comment={comment} />)}
                    </Wrap>
                </TabPanel>
            </TabPanels>
        </Tabs>
    </VStack>
};

export default Observed;