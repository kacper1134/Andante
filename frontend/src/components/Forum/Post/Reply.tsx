import {
  HStack,
  VStack,
  Spacer,
  Text,
  Box,
  Avatar,
  Icon,
} from "@chakra-ui/react";
import { useState } from "react";
import {
  postDescriptionFontSize,
  postHeaderFontSize,
  postImageSize,
} from "../common/ForumDimensions";
import PostLikeButton from "./PostLikeButton";
import parser from "html-react-parser";
import noprofile from "../../../static/noprofile.png";
import { useFirebase } from "../../../hooks/useFirebase";
import { RootState } from "../../../store";
import { useSelector } from "react-redux";
import { AiFillEdit } from "react-icons/ai";
import NewPostReply from "./NewPostReply";
import { useLikeResponseMutation } from "../../../store/api/forum-api-slice";
import { useNavigate, useParams } from "react-router-dom";
import { PostResponseLikeDTO } from "../../../store/api/result/dto/forum/PostResponseLikeDTO";

export type ReplyType = {
  id: number;
  date: string;
  image: string;
  user: string;
  content: string;
  likes: PostResponseLikeDTO[];
  numberOfLikes: number;
};

type ReplyProps = {
  reply: ReplyType;
  index: number;
  setReloadReplies: React.Dispatch<React.SetStateAction<boolean>>;
};

const Reply = ({ reply, index, setReloadReplies }: ReplyProps) => {
  const { id } = useParams();
  const [isEdit, setIsEdit] = useState(false);
  const profileImage = useFirebase(reply.image, noprofile);
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const isCurrentUserAuthor = userDetails?.personal.username === reply.user;
  const [likeResponse] = useLikeResponseMutation();
  const isLiked = reply.likes
    .map((like) => like.email)
    .includes(userDetails?.personal.emailAddress!);
  const navigate = useNavigate();

  const onLikeHandler = () => {
    likeResponse({
      body: {
        id: reply.id,
        email: userDetails?.personal.emailAddress!,
      },
      postId: +id!,
    }).then(() => setReloadReplies(true));
  };

  return (
    <HStack bg="primary.50" w="100%" p="20px" rounded="xl" textStyle="p">
      <VStack h="100%" minWidth={postImageSize}>
        <Avatar
          borderRadius="full"
          boxSize={postImageSize}
          src={profileImage}
          cursor="pointer"
          onClick={() => navigate(`/profile/community/${reply.user}`)}
        />
      </VStack>
      <Spacer />
      <VStack w="100%">
        <Text color="gray" fontSize={postHeaderFontSize} alignSelf="flex-start">
          #{index}{" "}
          <Text as="strong" color="primary.600" cursor="pointer">
            {reply.user}
          </Text>{" "}
          replied on {reply.date}
        </Text>
        {!isEdit && (
          <Box
            fontSize={postDescriptionFontSize}
            whiteSpace="pre-wrap"
            alignSelf="flex-start"
            wordBreak="break-word"
          >
            {parser(reply.content)}
          </Box>
        )}
        <NewPostReply
          replyId={reply.id}
          isOpen={isEdit}
          isEdit={true}
          setIsOpen={setIsEdit}
          setReloadReplies={setReloadReplies}
          initialText={reply.content}
        />

        <PostLikeButton
          isLiked={isLiked}
          numberOfLikes={reply.numberOfLikes}
          onLikeHandler={onLikeHandler}
          disabled={isCurrentUserAuthor}
        />
      </VStack>
      {isCurrentUserAuthor && (
        <HStack
          color="primary.600"
          textStyle="h1"
          cursor="pointer"
          alignSelf="flex-start"
          align="flex-start"
          h="100%"
          fontSize={postHeaderFontSize}
          onClick={() => setIsEdit((prev) => !prev)}
        >
          <Spacer />
          <Icon as={AiFillEdit} color="orange" />
          <Text>Edit</Text>
        </HStack>
      )}
    </HStack>
  );
};

export default Reply;
