import { Center, Text, useToken, VStack } from "@chakra-ui/react";
import { hexToRGB } from "../../../functions/style-functions";
import BlogContentForm from "./BlogContentForm";
import { headerSize } from "./BlogCreatePageSizes";

type BlogCreatePageProps = {
  topic: string;
  mode: "create" | "edit";
};

const BlogCreatePage = ({ topic, mode }: BlogCreatePageProps) => {
  const [color] = useToken("colors", ["primary.600"]);
  const backgroundColor = hexToRGB(color, 0.15);
  return (
    <VStack w="100%" h="100%" mb="10px">
      <Text
        w="100%"
        textAlign="center"
        bg={`linear-gradient(${backgroundColor},${backgroundColor}), url(https://images.unsplash.com/photo-1499750310107-5fef28a66643?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80)`}
        bgRepeat="no-repeat"
        bgSize="cover"
        bgPosition="center"
        h="16vh"
        fontSize={headerSize}
        textStyle="h1"
        color="white"
        as={Center}
      >
        {mode === "create" ? (
          <Text>Create a post</Text>
        ) : (
          <Text>Edit your post</Text>
        )}
      </Text>
      <BlogContentForm topic={topic} mode={mode} />
    </VStack>
  );
};

export default BlogCreatePage;
