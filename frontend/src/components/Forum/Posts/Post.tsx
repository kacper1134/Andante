import { VStack, Text, HStack } from "@chakra-ui/react";
import { Link, useNavigate } from "react-router-dom";
import {
  postHeaderFontSize,
  postTitleFontSize,
} from "../common/ForumDimensions";

export type PostType = {
  id: number;
  author: string;
  date: string;
  title: string;
  numberOfComments: number;
};

type PostProps = {
  post: PostType;
};

const months = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

export const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return (
    date.getDate().toString().padStart(2, "0") +
    " " +
    months[date.getMonth()] +
    " " +
    date.getFullYear() +
    " " +
    date.getHours().toString().padStart(2, "0") +
    ":" +
    date.getMinutes().toString().padStart(2, "0")
  );
};

const Post = ({ post }: PostProps) => {
  const postDate = formatDate(post.date);
  const navigate = useNavigate();

  return (
    <VStack
      width="100%"
      boxShadow={
        "0px 0px 0.8px rgba(0, 0, 0, 0.11)," +
        "0px 0px 1.7px rgba(0, 0, 0, 0.087)," +
        "0px 0px 2.9px rgba(0, 0, 0, 0.076)," +
        "0px 0px 4.3px rgba(0, 0, 0, 0.068)," +
        "0px 0px 6.3px rgba(0, 0, 0, 0.061)," +
        "0px 0px 8.9px rgba(0, 0, 0, 0.055)," +
        "0px 0px 12.6px rgba(0, 0, 0, 0.049)," +
        "0px 0px 18.3px rgba(0, 0, 0, 0.042)," +
        "0px 0px 28.1px rgba(0, 0, 0, 0.034)," +
        "0px 0px 50px rgba(0, 0, 0, 0.023)"
      }
      rounded="xl"
      px="20px"
    >
      <HStack pt="20px" w="100%" fontSize={postHeaderFontSize} textStyle="p">
        <Text color="gray">Created on {postDate} by</Text>{" "}
        <Text
          color="primary.500"
          fontWeight="bold"
          onClick={() => navigate(`/profile/community/${post.author}`)}
          cursor="pointer"
        >
          {post.author}
        </Text>
      </HStack>
      <Text
        as={Link}
        to={"../post/" + post.id}
        color="primary.500"
        fontWeight="semibold"
        w="100%"
        fontSize={postTitleFontSize}
        textStyle="p"
      >
        {post.title}
      </Text>
      <Text
        color="gray"
        w="100%"
        pb="20px"
        fontSize={postHeaderFontSize}
        textStyle="p"
      >
        {post.numberOfComments} replies
      </Text>
    </VStack>
  );
};

export default Post;
