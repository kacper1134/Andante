import { Center, HStack, Icon, IconButton, Spacer, Text, VStack } from "@chakra-ui/react";
import { useState } from "react";
import { AiFillLike } from "react-icons/ai";
import { useSelector } from "react-redux";
import { RootState } from "../../../../store";
import { useChangeCommentObservationStatusMutation } from "../../../../store/api/productSlice";
import { CommentOutputDTO } from "../../../../store/api/result/dto/product/CommentOutputDTO";
import ProductCommentContent from "./ProductCommentContent";
import ProductCommentHeader from "./ProductCommentHeader";

type ProductCommentProps = {
  comment: CommentOutputDTO,
  avatarUrl?: string,
  isLast: boolean,
};

const ProductComment = ({ comment, avatarUrl, isLast }: ProductCommentProps) => {
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const [changeCommentObservationStatus] = useChangeCommentObservationStatusMutation();
  const [isEdit, setIsEdit] = useState(false);
  
  const likeSize = {
    base: "16px",
    sm: "18px",
    md: "20px",
    lg: "22px",
    xl: "24px",
  };

  const changeStatus = () => {
    changeCommentObservationStatus({email: userDetails!.personal.emailAddress, id: comment.id});
  }
  
  return (
    <Center w="100%">
    <VStack
      mx="16px"
      my="8px"
      py={{ base: "16px", sm: "20px", md: "24px", lg: "28px", xl: "32px" }}
      height="fit-content"
      width="100%"
      bgColor="white"
      rounded="xl"
      boxShadow="1px 2px 2px 1px rgba(0,0,0,0.25)"
    >
      <ProductCommentHeader comment={comment} avatarUrl={avatarUrl} setIsEdit={setIsEdit} />
      <ProductCommentContent comment={comment} isEdit={isEdit} setIsEdit={setIsEdit} />
      <HStack alignSelf="end" pe="32px">
        <IconButton icon={<Icon as={AiFillLike} color="primary.400" boxSize={likeSize} />} 
          aria-label="like" bg="none" _hover={{bg: "none"}} _active={{bg: "none"}}
          disabled={!userDetails || userDetails.personal.username === comment.username} onClick={changeStatus} />
        <Text fontSize={likeSize} color="primary.600" textStyle="h3">{comment.observers.length}</Text>  
      </HStack>
    </VStack>
    </Center>
  );
};

export default ProductComment;
