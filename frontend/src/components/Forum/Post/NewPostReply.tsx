import {
  Box,
  Button,
  Collapse,
  Text,
  HStack,
  Icon,
  Spacer,
  VStack,
  useToast,
  Spinner,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { BiCommentAdd } from "react-icons/bi";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { RootState } from "../../../store";
import {
  useCreatePostResponseMutation,
  useCreateUserMutation,
  useUpdatePostReplyMutation,
} from "../../../store/api/forum-api-slice";
import UserTextEditor from "../../common/UserTextEditor";
import {
  postDescriptionFontSize,
  slideTextFontSize,
} from "../common/ForumDimensions";
import { useTranslation } from "react-i18next";

type NewPostReplyProps = {
  isOpen: boolean;
  isEdit: boolean;
  replyId?: number;
  initialText?: string;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setReloadReplies: React.Dispatch<React.SetStateAction<boolean>>;
};

const NewPostReply = ({
  isOpen,
  initialText,
  isEdit,
  replyId,
  setIsOpen,
  setReloadReplies,
}: NewPostReplyProps) => {
  const { id } = useParams();
  const [content, setContent] = useState(initialText ?? "");
  const [saving, setSaving] = useState(false);
  const [createUser] = useCreateUserMutation();
  const [createPostReply] = useCreatePostResponseMutation();
  const [updatePostReply] = useUpdatePostReplyMutation();
  const userData = useSelector((state: RootState) => state.auth.userDetails);
  const toast = useToast();

  useEffect(() => {
    if (isOpen) {
      setContent(initialText ?? "");
    }
  }, [initialText, isOpen]);

  const replySubmitHandler = () => {
    if (content === "") {
      showToast("Invalid content", "error", "Your reply cannot be empty!");
      return;
    }
    setSaving(true);
    createUser({
      email: userData?.personal.emailAddress!,
      name: userData?.personal.name!,
      username: userData?.personal.username!,
      surname: userData?.personal.surname!,
      posts: [],
      responses: [],
      likedPosts: [],
      likedResponses: [],
    })
      .then((result) => {
        const email = (result as any)?.data;
        createPostReply({
          content: content,
          postId: +id!,
          email: email,
        }).then(() => {
          if (!("error" in result)) {
            showToast("Create reply", "success", "Your reply was created successfully!");
            setIsOpen(false);
            setReloadReplies(true);
            setContent("");
          } else {
            showToast("Invalid data", "error", (result.error as any).data);
          }
        });
      })
      .finally(() => setSaving(false));
  };

  const replyEditHandler = () => {
    if (content === "") {
      showToast("Invalid content", "error", "Your reply cannot be empty!");
      return;
    }
    setSaving(true);
    createUser({
      email: userData?.personal.emailAddress!,
      name: userData?.personal.name!,
      username: userData?.personal.username!,
      surname: userData?.personal.surname!,
      posts: [],
      responses: [],
      likedPosts: [],
      likedResponses: [],
    })
      .then((result) => {
        const email = (result as any)?.data;
        updatePostReply({
          id: replyId,
          content: content,
          postId: +id!,
          email: email,
        }).then((result) => {
          if (!("error" in result)) {
            showToast("Update reply", "success", "Your reply was updated successfully!");
            setIsOpen(false);
            setReloadReplies(true);
          } else {
            showToast("Invalid data", "error", (result.error as any).data);
          }
        });
      })
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

  const { t } = useTranslation();

  return (
    <VStack
      as={Collapse}
      in={isOpen}
      w="100%"
      rounded="2xl"
      mt="10px"
      bg="white"
      boxShadow="1px 2px 2px 1px rgba(0,0,0,0.25)"
      textStyle="p"
    >
      <Text
        w="95%"
        fontSize={slideTextFontSize}
        mx="2.5%"
        my="10px"
        textStyle="h1"
      >
        {t("forum-section.your-reply")}
      </Text>

      <Box w="100%" px="2.5%">
        <UserTextEditor
          content={content}
          setContent={setContent}
          initialValue={initialText}
        />
      </Box>

      <HStack w="100%" px="2.5%" py="10px">
        <Spacer />
        <Button
          h="fit-content"
          py="5px"
          colorScheme="primary"
          fontSize={postDescriptionFontSize}
          onClick={isEdit ? replyEditHandler : replySubmitHandler}
          disabled={saving}
        >
          {saving && <Spinner />}
          {!saving && (
            <>
              {isEdit ? t("forum-section.post-edit") : t("forum-section.post-save")}
              <Icon ml="10px" as={BiCommentAdd} />
            </>
          )}
        </Button>
      </HStack>
    </VStack>
  );
};

export default NewPostReply;
