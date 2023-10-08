import { HStack, Text, Spacer, VStack, Wrap, useToast } from '@chakra-ui/react';
import ProfilePost from './ProfilePost';
import { useEffect, useState } from 'react';
import { CommentOutputDTO, isCommentOutputDTOArray } from '../../../store/api/result/dto/product/CommentOutputDTO';
import { useGetTopCommentsByUserQuery } from '../../../store/api/productSlice';
import { UserDetails } from '../../../utils/KeycloakUtils';

export interface TopPostsProps {
    userDetails: UserDetails,
}

const TopPosts: React.FC<TopPostsProps> = ({ userDetails }) => {
    const [topComments, setTopComments] = useState<CommentOutputDTO[]>([]);
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const { data } = useGetTopCommentsByUserQuery({username: userDetails!.personal.username, page: 0, size: 4}, {refetchOnMountOrArgChange: true});
    const toast = useToast();

    useEffect(() => {
        if (data) {
            if (isCommentOutputDTOArray(data)) {
                setTopComments(data);
            } else {
                setErrorMessages(data);
            }
        }
    }, [data]);

    useEffect(() => {
        errorMessages.forEach(message => toast({
          title: "Something went wrong",
          description: message,
          status: "error",
          duration: 9000,
          isClosable: true,
        }))
      }, [errorMessages]);

    return (
        <VStack w="100%" bg="purple.50" px="16px" py="8px">
            <HStack alignSelf="start">
                <Text color="primary.400" textStyle="h3" fontSize="16px" userSelect="none" alignSelf="start">TOP POSTS & COMMENTS</Text>
            </HStack>
            <Wrap spacing="16px" alignSelf="start">
            {topComments.map((post, index) => <ProfilePost key={index} comment={post} />)}
            </Wrap>
            <Spacer />  
        </VStack>
    )
}

export default TopPosts;