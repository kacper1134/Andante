import { HStack, Icon, Spacer, Tooltip, Text } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { AiFillLike, AiOutlineLike } from "react-icons/ai";
import {
  postDescriptionFontSize,
  postHeaderFontSize,
} from "../common/ForumDimensions";

type PostLikeButtonProps = {
  isLiked: boolean;
  onLikeHandler: () => void;
  disabled: boolean;
  numberOfLikes: number;
};

const PostLikeButton = ({
  isLiked,
  numberOfLikes,
  onLikeHandler,
  disabled,
}: PostLikeButtonProps) => {
  return (
    <HStack alignSelf="flex-start">
      <Tooltip
        hasArrow
        label={!isLiked ? "Like" : "Unlike"}
        fontSize={postHeaderFontSize}
        bg="primary.600"
        mt="10px"
        textStyle="p"
        isDisabled={disabled}
      >
        <HStack
          height="fit-content"
          position="relative"
          onClick={disabled ? undefined : onLikeHandler}
        >
          <Icon
            fontSize={postDescriptionFontSize}
            color={disabled ? "gray" : "primary.600"}
            as={AiOutlineLike}
            cursor={disabled ? "default" : "pointer"}
            position="absolute"
            left="0"
          />
          <HStack
            key={isLiked ? 1 : 0}
            as={motion.div}
            whileHover={disabled ? undefined : { opacity: 1 }}
            initial={{ opacity: isLiked ? 1 : 0 }}
          >
            <Icon
              fontSize={postDescriptionFontSize}
              color={disabled ? "gray" : "primary.600"}
              as={AiFillLike}
              cursor={disabled ? "default" : "pointer"}
              position="absolute"
              left="0"
            />
          </HStack>
        </HStack>
      </Tooltip>
      <Spacer />
      <Text fontSize={postHeaderFontSize} textStyle="p">
        {numberOfLikes} likes
      </Text>
    </HStack>
  );
};

export default PostLikeButton;
