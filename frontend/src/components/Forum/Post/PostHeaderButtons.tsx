import { HStack, Spacer } from "@chakra-ui/react";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { RootState } from "../../../store";
import { useLikePostMutation } from "../../../store/api/forum-api-slice";
import { PostType } from "./Post";
import PostLikeButton from "./PostLikeButton";

type PostHeaderButtonsProps = {
  post: PostType;
  setReloadPost: React.Dispatch<React.SetStateAction<boolean>>;
};

const PostHeaderButtons = ({ post, setReloadPost }: PostHeaderButtonsProps) => {
  const { id } = useParams();
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const isCurrentUserAuthor = userDetails?.personal.username === post.user;
  const isLiked = post.likes
    .map((like) => like.email)
    .includes(userDetails?.personal.emailAddress!);
  const [likePost] = useLikePostMutation();

  const onLikeHandler = () => {
    likePost({
      body: {
        id: +id!,
        email: userDetails?.personal.emailAddress!,
      },
      topicId: post.topicId!,
    }).then(() => setReloadPost(true));
  };

  return (
    <HStack w="100%">
      <PostLikeButton
        isLiked={isLiked}
        numberOfLikes={post.numberOfLikes}
        disabled={isCurrentUserAuthor}
        onLikeHandler={onLikeHandler}
      />
      <Spacer />
    </HStack>
  );
};

export default PostHeaderButtons;
