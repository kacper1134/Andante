import { Box, Button, Text, Icon, HStack, Spinner } from "@chakra-ui/react";
import { useState } from "react";
import { IoAddCircleSharp } from "react-icons/io5";
import PageChanger from "../../../Shop/ProductPage/ProductComments/PageChanger";
import {
  blogContentFontSize,
  commentHeaderFontSize,
} from "../BlogPostPageSizes";
import NewPostComment from "./NewPostComment";
import PostComment from "./PostComment";

type PostCommentsProps = {
  postCommentsData: any[];
  postTitle: string;
  postSlug: any;
  setReloadComments: React.Dispatch<React.SetStateAction<boolean>>;
  userImages: Map<string, string> | undefined;
  isCommentsDownload: boolean;
};

const COMMENT_PER_PAGE = 6;

export type UserProfileImage = {
  username: string;
  imageUrl: string;
};

const PostComments = ({
  postCommentsData,
  postTitle,
  postSlug,
  setReloadComments,
  userImages,
  isCommentsDownload,
}: PostCommentsProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [page, setPage] = useState(0);

  const comments = postCommentsData.slice(
    page * COMMENT_PER_PAGE,
    (page + 1) * COMMENT_PER_PAGE
  );

  const numberOfPages = Math.ceil(postCommentsData.length / COMMENT_PER_PAGE);

  if (page >= numberOfPages && numberOfPages !== 0) {
    setPage(0);
  }

  return (
    <Box position="relative">
      <HStack>
        <Text
          color="primary.500"
          fontWeight="bold"
          fontSize={commentHeaderFontSize}
          my="16px"
          textStyle="p"
        >
          Comments
        </Text>
        <Button
          colorScheme="primary"
          h="fit-content"
          py="5px"
          onClick={() => setIsOpen((prev) => !prev)}
          fontSize={blogContentFontSize}
          textStyle="p"
        >
          New <Icon as={IoAddCircleSharp} ml="3" />
        </Button>
      </HStack>
      <NewPostComment
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        postTitle={postTitle}
        postSlug={postSlug}
        setReloadComments={setReloadComments}
      />
      {isCommentsDownload && <Spinner size="lg" color="primary.900" />}
      {comments &&
        comments.map((comment, index) => (
          <PostComment
            key={index}
            comment={comment}
            setReloadComments={setReloadComments}
            userImage={
              userImages?.has(comment.username)
                ? userImages.get(comment.username)!
                : ""
            }
          />
        ))}
      {numberOfPages > 0 && (
        <PageChanger
          page={page}
          setPage={setPage}
          numberOfPages={numberOfPages}
        />
      )}
    </Box>
  );
};

export default PostComments;
