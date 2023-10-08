import {
  Collapse,
  HStack,
  VStack,
  Button,
  Spacer,
  Spinner,
  useToast,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { writeClient } from "../../../../client";
import UserTextEditor from "../../../common/UserTextEditor";
import { blogContentFontSize } from "../BlogPostPageSizes";

type EditPostCommentProps = {
  isEdit: boolean;
  initialContent: string;
  commentId: string;
  setIsEdit: React.Dispatch<React.SetStateAction<boolean>>;
  setReloadComments: React.Dispatch<React.SetStateAction<boolean>>;
};

const EditPostComment = ({
  isEdit,
  setIsEdit,
  initialContent,
  commentId,
  setReloadComments,
}: EditPostCommentProps) => {
  const [content, setContent] = useState("");
  const [saving, setSaving] = useState(false);
  const toast = useToast();

  useEffect(() => {
    if (isEdit) {
      setContent(initialContent);
    }
  }, [initialContent, isEdit]);

  const onConfirmHandler = () => {
    if (!content) {
      showToast(
        "Comment error",
        "error",
        "You cannot sent comment with empty body!"
      );
      return;
    }

    setSaving(true);
    writeClient
      .patch(commentId)
      .set({ content })
      .commit()
      .then(() => {
        showToast("Update comment", "success", "Your comment was updated!");
        setIsEdit(false);
        setReloadComments(true);
      })
      .catch(() =>
        showToast(
          "Comment error",
          "error",
          "You cannot sent comment now. Please try again later!"
        )
      )
      .finally(() => setSaving(false));
  };

  const showToast = (
    title: string,
    status: "success" | "error",
    message: string
  ) => {
    toast({
      title: title,
      description: message,
      status: status,
      isClosable: true,
      duration: 2000,
    });
  };

  return (
    <VStack as={Collapse} in={isEdit}>
      <UserTextEditor
        content={content}
        setContent={setContent}
        initialValue={initialContent}
      />
      <HStack w="100%">
        <Spacer />
        <Button
          colorScheme="gray"
          onClick={() => setIsEdit(false)}
          fontSize={blogContentFontSize}
          h="fit-content"
          py="5px"
          disabled={saving}
        >
          {!saving ? "Cancel" : <Spinner />}
        </Button>
        <Button
          colorScheme="primary"
          onClick={onConfirmHandler}
          fontSize={blogContentFontSize}
          h="fit-content"
          py="5px"
          disabled={saving}
        >
          {!saving ? "Confirm" : <Spinner />}
        </Button>
      </HStack>
    </VStack>
  );
};

export default EditPostComment;
