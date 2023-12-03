import { HStack, VStack, Image, Text, Box } from "@chakra-ui/react";
import { motion } from "framer-motion";
import parse from "html-react-parser";
import { useNavigate } from "react-router-dom";

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
  const textColor = "primary.700";
  const textSize = { base: "md", lg: "lg" };

  const navigate = useNavigate();

  const handlePostClick = () => {
    navigate("/reviews/" + post.name.trim().toLowerCase().replaceAll(" ", "-"));
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      whileInView={{ opacity: 1 }}
      transition={{ duration: 3, delay: 0.2 }}
      viewport={{ once: true }}
      onClick={handlePostClick}
      style={{ cursor: "pointer" }}
    >
      <Box
        p={6}
        borderRadius="lg"
        background="white"
        boxShadow="md"
        transition="box-shadow 0.3s, transform 0.3s"
        _hover={{
          boxShadow: "lg",
          transform: "translateY(-5px)",
        }}
      >
        <HStack spacing={{ base: 4, lg: 8 }} maxWidth={{ lg: "800px" }}>
          <Image
            src={`${post.url}`}
            boxSize={{ base: "150px", md: "200px", lg: "400px" }}
            fit="cover"
            borderRadius="md"
          />
          <VStack alignItems="flex-start" flex="1" pl={{ base: 0, lg: 4 }}>
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
            <Box
              noOfLines={{ base: 3, md: 6, lg: 9 }}
              fontSize={textSize}
              textStyle="p"
              mt={2}
            >
              {parse(post.description)}
            </Box>
          </VStack>
        </HStack>
      </Box>
    </motion.div>
  );
};

export default BlogPost;