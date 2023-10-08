import {
  VStack,
  Text,
  HStack,
  Spacer,
  Avatar,
  Box,
  Icon,
  Button,
} from "@chakra-ui/react";
import {
  buttonTextFontSize,
  postDescriptionFontSize,
  postHeaderFontSize,
  postImageSize,
  postTitleFontSize,
} from "../common/ForumDimensions";
import { useState } from "react";
import PostHeaderButtons from "./PostHeaderButtons";
import parser from "html-react-parser";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { useNavigate } from "react-router-dom";
import { useFirebase } from "../../../hooks/useFirebase";
import noprofile from "../../../static/noprofile.png";
import { RootState } from "../../../store";
import { useSelector } from "react-redux";
import { AiFillDelete, AiFillEdit } from "react-icons/ai";
import { FaReply } from "react-icons/fa";
import NewPostForm from "../Posts/NewPost/NewPostForm";
import { PostResponseLikeDTO } from "../../../store/api/result/dto/forum/PostResponseLikeDTO";
import DeletePostModal from "./DeletePostModal";

export type PostType = {
  title: string;
  date: string;
  category: TopicOutputDTO;
  image: string;
  user: string;
  content: string;
  topicId: number;
  numberOfLikes: number;
  likes: PostResponseLikeDTO[];
};

type PostProps = {
  post: PostType;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setReloadPost: React.Dispatch<React.SetStateAction<boolean>>;
};

const Post = ({ post, setIsOpen, setReloadPost }: PostProps) => {
  const [isEdit, setIsEdit] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const navigate = useNavigate();
  const profileImage = useFirebase(post.image, noprofile);
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const isCurrentUserAuthor = userDetails?.personal.username === post.user;

  return (
    <>
      <Text
        w="100%"
        p="1%"
        fontWeight="bold"
        fontSize={postTitleFontSize}
        bg="primary.400"
        color="white"
        textStyle="p"
      >
        {post.title}
      </Text>
      <HStack bg="primary.100" w="100%" p="20px" rounded="xl">
        <VStack h="100%" minWidth={postImageSize}>
          <Avatar
            boxSize={postImageSize}
            src={profileImage}
            cursor="pointer"
            onClick={() => navigate(`/profile/community/${post.user}`)}
          />
        </VStack>
        <Spacer />
        <VStack w="100%" textStyle="p">
          <Box
            color="gray"
            fontSize={postHeaderFontSize}
            alignSelf="flex-start"
          >
            Post created on {post.date} in{" "}
            <Text
              as="strong"
              color="primary.600"
              cursor="pointer"
              onClick={() => navigate(`../topic/${post.category.id}`)}
            >
              {post.category.name}
            </Text>{" "}
            by{" "}
            <Text as="strong" color="primary.600" cursor="pointer">
              {post.user}
            </Text>
          </Box>
          {!isEdit && (
            <Box
              fontSize={postDescriptionFontSize}
              whiteSpace="pre-wrap"
              alignSelf="flex-start"
              wordBreak="break-word"
            >
              {parser(post.content)}
            </Box>
          )}
          <NewPostForm
            isOpen={isEdit}
            isEdit={true}
            initialTitle={post.title}
            initialContent={post.content}
            topicId={post.topicId}
            setIsOpen={setIsEdit}
            setReload={setReloadPost}
          />
          <PostHeaderButtons post={post} setReloadPost={setReloadPost} />
        </VStack>

        <VStack
          color="primary.600"
          textStyle="h1"
          cursor="pointer"
          alignSelf="flex-start"
          h="100%"
          fontSize={postHeaderFontSize}
        >
          {isCurrentUserAuthor && (
            <HStack w="100%" onClick={() => setIsEdit((prev) => !prev)}>
              <Spacer />
              <Icon as={AiFillEdit} color="orange" />
              <Text>Edit</Text>
            </HStack>
          )}
          {isCurrentUserAuthor && (
            <HStack w="100%" onClick={() => setIsDeleteModalOpen(true)}>
              <Spacer />
              <Icon as={AiFillDelete} color="red" />
              <Text>Delete</Text>
            </HStack>
          )}
          <Spacer />
          <Button
            h="fit-content"
            colorScheme="primary"
            py="5px"
            fontSize={buttonTextFontSize}
            onClick={() => setIsOpen((prev) => !prev)}
          >
            Reply <Icon ml="10px" as={FaReply} />
          </Button>
        </VStack>
      </HStack>
      <DeletePostModal
        isOpen={isDeleteModalOpen}
        setIsOpen={setIsDeleteModalOpen}
        title={post.title}
        topicId={post.topicId}
      />
    </>
  );
};

export default Post;
