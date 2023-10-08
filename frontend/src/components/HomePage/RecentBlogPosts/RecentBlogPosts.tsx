import { Button, Text, SimpleGrid, VStack } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { readClient } from "../../../client";
import BlogPost from "./BlogPost";
import { BlogPostType } from "./BlogPost";

const RecentBlogPosts = () => {
  const [recentPosts, setRecentPosts] = useState<BlogPostType[]>();
  const navigate = useNavigate();

  useEffect(() => {
    const query = `*[_type == "post" && category == "reviews"] | order(publishedAt desc)[0...4] {
        _id,
        title,
        description,
        image,
        "imageUrl": image.asset->url,
        author,
        publishedAt,
        slug
      }`;

    readClient
      .fetch(query)
      .then((reviews) =>
        setRecentPosts(
          reviews.map((review: any) => {
            const publishDay = new Date(review.publishedAt);
            return {
              id: review._id,
              date:
                publishDay.getFullYear() +
                "-" +
                publishDay.getMonth().toString().padStart(2, "0") +
                "-" +
                publishDay.getDate().toString().padStart(2, "0"),
              author: review.author,
              name: review.title,
              description: review.description,
              url: review.imageUrl,
            };
          })
        )
      )
      .catch(console.log);
  }, []);

  return (
    <VStack
      bgImage="url(/blogReviewBackground.png)"
      bgRepeat="no-repeat"
      bgSize="cover"
      bgPosition="center"
    >
      <Text
        pt="8"
        color="primary.600"
        fontSize={{ base: "3xl", lg: "5xl" }}
        textStyle="h1"
      >
        RECENT REVIEWS
      </Text>
      <SimpleGrid columns={{ base: 1, xl: 2 }} spacing={10}>
        {recentPosts?.map((post) => (
          <BlogPost key={post.id} post={post} />
        ))}
      </SimpleGrid>
      <motion.div
        whileHover={{
          scale: 1.1,
        }}
      >
        <Button
          colorScheme="purple"
          color="white"
          borderRadius="2xl"
          fontWeight="bold"
          size={{ base: "md", md: "lg" }}
          mb="20px"
          textStyle="p"
          onClick={() => navigate("/reviews")}
        >
          Read More Reviews
        </Button>
      </motion.div>
    </VStack>
  );
};

export default RecentBlogPosts;
