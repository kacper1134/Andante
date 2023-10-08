import { HStack, VStack, Image, Text, Box } from "@chakra-ui/react";
import { motion } from "framer-motion";
import parse from "html-react-parser";

export type BlogPostType = {
  id: number;
  date: string;
  author: string;
  name: string;
  description: string;
  url: string;
};

type BlogPostProps = {
  post: BlogPostType;
};

const BlogPost = ({ post }: BlogPostProps) => {
  const textColor = "primary.500";
  const textSize = { base: "md", lg: "lg" };
  return (
    <motion.div
      initial={{ opacity: 0 }}
      whileInView={{ opacity: 1 }}
      transition={{ duration: 3, delay: 0.2 }}
      viewport={{ once: true }}
    >
      <HStack m={15} maxWidth={{ lg: "800px" }}>
        <Image
          src={`${post.url}`}
          boxSize={{ base: "150px", md: "200px", lg: "400px" }}
          fit="scale-down"
        />
        <VStack alignItems="flex-start" pl={{ base: 5, lg: 10 }}>
          <Text color={textColor} fontSize={textSize} textStyle="p">
            {post.date} | {post.author}
          </Text>
          <Text
            fontWeight="semibold"
            textStyle="h1"
            color={textColor}
            fontSize={{ base: "xl", lg: "3xl" }}
          >
            {post.name}
          </Text>
          <Box noOfLines={{ base: 3, md: 6, lg: 9 }} fontSize={textSize} textStyle="p">
            {parse(post.description)}
          </Box>
        </VStack>
      </HStack>
    </motion.div>
  );
};

export default BlogPost;
