import {
  Button,
  Collapse,
  HStack,
  Icon,
  Input,
  VStack,
  Text,
  Spinner,
  useToast,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { GoChecklist } from "react-icons/go";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { RootState } from "../../../../store";
import {
  useCreatePostMutation,
  useCreateUserMutation,
  useUpdatePostMutation,
} from "../../../../store/api/forum-api-slice";
import UserTextEditor from "../../../common/UserTextEditor";
import {
  newPostTitleFontSize,
  buttonTextFontSize,
} from "../../common/ForumDimensions";

type NewPostFormProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setReload: React.Dispatch<React.SetStateAction<boolean>>;
  initialTitle?: string;
  initialContent?: string;
  topicId?: number;
  isEdit?: boolean;
};

const NewPostForm = ({
  isOpen,
  setReload,
  setIsOpen,
  isEdit,
  topicId,
  initialTitle,
  initialContent,
}: NewPostFormProps) => {
  const { id } = useParams();
  const [content, setContent] = useState(initialContent ?? "");
  const [title, setTitle] = useState(initialTitle ?? "");
  const userData = useSelector((state: RootState) => state.auth.userDetails);
  const [createUser] = useCreateUserMutation();
  const [createPost] = useCreatePostMutation();
  const [updatePost] = useUpdatePostMutation();
  const [saving, setSaving] = useState(false);
  const toast = useToast();

  useEffect(() => {
    if(isOpen) {
      setTitle(initialTitle ?? "");
      setContent(initialContent ?? "");
    }
  }, [initialContent, initialTitle, isOpen])

  const onPostSubmit = () => {
    if (title === "") {
      showToast("Invalid data", "error", "Post title must not be empty");
      return;
    }
    if (content === "") {
      showToast("Invalid data", "error", "Post content must not be empty");
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
        createPost({
          title: title,
          content: content,
          topicId: +id!,
          email: email,
        }).then((result) => {
          if (!("error" in result)) {
            showToast("Create post", "success", "Your post was created successfully!");
            setReload(true);
            setTitle("");
            setContent("");
            setIsOpen(false);
          } else {
            showToast("Invalid data", "error", (result.error as any).data);
          }
        });
      })
      .finally(() => setSaving(false));
  };

  const onPostUpdate = () => {
    if (title === "") {
      showToast("Invalid data", "error", "Post title must not be empty");
      return;
    }
    if (title.length > 150) {
      showToast("Invalid data", "error", "Post title size must not exceed 150 characters");
      return;
    }
    if (content === "") {
      showToast("Invalid data", "error", "Post content must not be empty");
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
        updatePost({
          id: +id!,
          title: title,
          content: content,
          topicId: topicId!,
          email: email,
        }).then((result) => {
          if (!("error" in result)) {
            showToast("Update post", "success", "Your post was updated successfully!");
            setReload(true);
            setIsOpen(false);
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

  return (
    <VStack
      w="100%"
      as={Collapse}
      in={isOpen}
      rounded="2xl"
      bg="white"
      boxShadow="1px 2px 2px 1px rgba(0,0,0,0.25)"
    >
      <VStack my="2%" px="2%" spacing="2%" alignContent="center">
        <Input
          placeholder="Your amazing title"
          h="fit-content"
          fontSize={newPostTitleFontSize}
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          fontWeight="bold"
          textStyle="p"
        />
        <UserTextEditor
          content={content}
          setContent={setContent}
          initialValue={initialContent}
        />

        <Button
          fontSize={buttonTextFontSize}
          height="fit-content"
          colorScheme="primary"
          alignSelf="flex-start"
          py="1.5"
          onClick={isEdit ? onPostUpdate : onPostSubmit}
          disabled={saving}
        >
          {saving && <Spinner />}
          {!saving && (
            <HStack>
              <Text textStyle="p" fontWeight="semibold">
                {isEdit ? "Edit your post" : "Save your post"}
              </Text>
              <Icon as={GoChecklist} />
            </HStack>
          )}
        </Button>
      </VStack>
    </VStack>
  );
};

export default NewPostForm;
