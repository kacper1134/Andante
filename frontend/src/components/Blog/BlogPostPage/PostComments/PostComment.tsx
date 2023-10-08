import { Box, Flex, Text, Icon, HStack, Spacer, VStack } from "@chakra-ui/react";
import {
  blogContentFontSize,
  commentEditButtonFontSize,
} from "../BlogPostPageSizes";
import PostCommentHeader from "./PostCommentHeader";
import parse from "html-react-parser";
import { AiFillEdit } from "react-icons/ai";
import { useState } from "react";
import EditPostComment from "./EditPostComment";
import { RootState } from "../../../../store";
import { useSelector } from "react-redux";

type PostCommentProps = {
  comment: any;
  setReloadComments: React.Dispatch<React.SetStateAction<boolean>>;
  userImage: string;
};

const PostComment = ({
  comment,
  setReloadComments,
  userImage,
}: PostCommentProps) => {
  const [isEdit, setIsEdit] = useState(false);
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const isCurrentUserAuthor =
    userDetails?.personal.username === comment.username;

  return (
    <Box
      position="relative"
      borderRadius={"8px"}
      p="16px"
      mt="16px"
      mb="16px"
      boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
    >
      {isCurrentUserAuthor && (
        <HStack
          color="primary.600"
          textStyle="h1"
          fontSize={commentEditButtonFontSize}
          w="100%"
        >
          <Spacer />
          <HStack cursor="pointer" onClick={() => setIsEdit(true)}>
            <Icon as={AiFillEdit} color="orange" cursor="pointer" />
            <Text>Edit</Text>
          </HStack>
        </HStack>
      )}
      <Flex align="center" m="4px" p="4px">
        <PostCommentHeader
          author={comment.username}
          date={comment.publishedAt}
          userImage={userImage}
        />
      </Flex>
      <EditPostComment
        isEdit={isEdit}
        setIsEdit={setIsEdit}
        initialContent={comment.content}
        commentId={comment?._id}
        setReloadComments={setReloadComments}
      />
      {!isEdit && (
        <Box fontSize={blogContentFontSize} m="4px" p="4px" textStyle="p">
          {parse(comment.content)}
        </Box>
      )}
    </Box>
  );
};

export default PostComment;
