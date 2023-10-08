import {
  Button,
  Collapse,
  Flex,
  Icon,
  Spinner,
  useToast,
} from "@chakra-ui/react";
import { DateTime } from "luxon";
import { useState } from "react";
import { IoMdSend } from "react-icons/io";
import { useSelector } from "react-redux";
import { writeClient } from "../../../../client";
import { RootState } from "../../../../store";
import UserTextEditor from "../../../common/UserTextEditor";
import { blogContentFontSize } from "../BlogPostPageSizes";

type NewPostCommentProps = {
  isOpen: boolean;
  postTitle: string;
  postSlug: any;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setReloadComments: React.Dispatch<React.SetStateAction<boolean>>;
};

const NewPostComment = ({
  isOpen,
  setIsOpen,
  postTitle,
  postSlug,
  setReloadComments,
}: NewPostCommentProps) => {
  const [content, setContent] = useState("");
  const [saving, setSaving] = useState(false);
  const userData = useSelector((state: RootState) => state.auth.userDetails);

  const toast = useToast();

  const onSentHandler = () => {
    if (!content) {
      showToast(
        "Comment error",
        "You cannot sent comment with empty body!",
        "error"
      );
      return;
    }

    setSaving(true);
    
    const newBlogPostCommentQuery = {
      _type: "comment",
      author: userData?.personal.name + " " + userData?.personal.surname,
      username: userData?.personal.username,
      postTitle,
      slug: postSlug?.current,
      publishedAt: DateTime.now(),
      content,
    };

    writeClient
      .create(newBlogPostCommentQuery)
      .then(() => {
        showToast("New comment", "Your comment was sent!", "success");
        setReloadComments(true);
        setIsOpen(false);
        setContent("");
      })
      .catch(() =>
        showToast(
          "Comment error",
          "You cannot sent comment now. Please try again later!",
          "error"
        )
      )
      .finally(() => setSaving(false));
  };

  const showToast = (
    title: string,
    description: string,
    status: "success" | "error"
  ) => {
    toast({
      title,
      description,
      status,
      isClosable: true,
      duration: 2000,
    });
  };

  return (
    <Collapse in={isOpen}>
      <UserTextEditor content={content} setContent={setContent} />
      <Flex direction="column" align="flex-end">
        <Button
          colorScheme="purple"
          variant="solid"
          mt="8px"
          py="5px"
          h="fit-content"
          fontSize={blogContentFontSize}
          textStyle="p"
          onClick={onSentHandler}
          disabled={saving}
        >
          {!saving ? (
            <>
              Send <Icon as={IoMdSend} ml="3" />
            </>
          ) : (
            <Spinner />
          )}
        </Button>
      </Flex>
    </Collapse>
  );
};

export default NewPostComment;
