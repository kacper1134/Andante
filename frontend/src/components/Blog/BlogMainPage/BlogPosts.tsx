import {
  Text,
  Box,
  VStack,
  Center,
  useToken,
  Button,
  Icon,
  HStack,
  Spinner,
} from "@chakra-ui/react";
import React, { useEffect, useState } from "react";
import BlogPost from "./BlogPost";
import { readClient } from "../../../client.js";
import { hexToRGB } from "../../../functions/style-functions";
import {
  postAuthorFontSize,
  postTitleMainPageFontSize,
  quoteFontSize,
} from "./BlogMainPageSizes";
import { IoAddCircleSharp } from "react-icons/io5";
import { Link } from "react-router-dom";
import BlogFilter from "./BlogFilter";
import PageChanger from "../../Shop/ProductPage/ProductComments/PageChanger";
import { useKeycloak } from "@react-keycloak/web";
import { KeycloakRole } from "../../../enums/KeycloakRole";

export interface BlogPostsProps {
  topic: "music" | "reviews" | "recommended";
}

const imageVariants = {
  music: {
    image:
      "https://images.unsplash.com/photo-1475744214834-0cb9be6eb226?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80",
    quote:
      "Music expresses that which cannot be said and on which it is impossible to be silent",
  },
  reviews: {
    image:
      "https://images.unsplash.com/photo-1585909695284-32d2985ac9c0?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80",
    quote:
      "Twice and thrice over, as they say, good is it to repeat and review what is good",
  },
  recommended: {
    image:
      "https://images.unsplash.com/photo-1585853565090-5227d79f2171?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1631&q=80",
    quote: "The hardest thing is writing a recommendation for someone we know",
  },
};

const POSTS_PER_PAGE = 4;

const BlogPosts: React.FC<BlogPostsProps> = ({ topic }) => {
  const [allPostsData, setAllPosts] = useState<any[]>([]);
  const [color] = useToken("colors", ["primary.600"]);
  const backgroundColor = hexToRGB(color, 0.15);
  const [searchTerm, setSearchTerm] = useState("");
  const [updated, setUpdated] = useState(false);
  const [page, setPage] = useState(0);
  const { keycloak } = useKeycloak();

  const [isDownload, setIsDownload] = useState(false);

  useEffect(() => {
    const query = `*[_type == "post" && category == "${topic}" ${
      searchTerm && `&& title match "${searchTerm}*"`
    }] | order(publishedAt desc) {
        _id,
        title,
        description,
        image,
        "imageUrl": image.asset->url,
        author,
        publishedAt,
        slug
      }`;
    setIsDownload(true);
    readClient
      .fetch(query)
      .then((data) => setAllPosts(data))
      .catch(console.log)
      .finally(() => setIsDownload(false));
    setUpdated(false);
  }, [searchTerm, topic, updated]);

  const isNotEmpty =
    allPostsData.length > 0 ||
    !(searchTerm === "" && allPostsData.length === 0);

  const pagePosts = allPostsData.slice(
    page * POSTS_PER_PAGE,
    (page + 1) * POSTS_PER_PAGE
  );
  const numberOfPages = Math.ceil(allPostsData.length / POSTS_PER_PAGE);

  if (page >= numberOfPages && numberOfPages !== 0) {
    setPage(0);
  }

  return (
    <>
      <Box
        bgImage={`linear-gradient(${backgroundColor},${backgroundColor}), url(${imageVariants[topic].image})`}
        bgRepeat="no-repeat"
        bgSize="cover"
        bgPosition="center"
        w="100%"
        minH="80vh"
      >
        <Center h="100%">
          <Text w="60%" textStyle="h1" fontSize={quoteFontSize} color="white">
            {imageVariants[topic].quote}
          </Text>
        </Center>
      </Box>
      <VStack w="100%" py="30px">
        <HStack h="100%" mb="20px">
          <Text
            textStyle="h1"
            fontSize={postTitleMainPageFontSize}
            color="primary.600"
          >
            {isNotEmpty ? "Latest posts" : "There is nothing here yet"}
          </Text>
          {keycloak.hasRealmRole(KeycloakRole.BLOGGER) &&
          <Button
            as={Link}
            to="create"
            colorScheme="primary"
            fontSize={postAuthorFontSize}
            h="fit-content"
            py="5px"
            textStyle="p"
          >
            Add new post <Icon as={IoAddCircleSharp} ml="3" />
          </Button>}
        </HStack>
        {isNotEmpty && <BlogFilter setSearch={setSearchTerm} />}
        {isDownload && (
          <Box>
            <Spinner size="lg" color="primary.900" />
          </Box>
        )}
        <VStack w="100%" spacing={10}>
          {allPostsData &&
            pagePosts.map((post, index) => (
              <BlogPost key={index} post={post} setUpdated={setUpdated} />
            ))}
        </VStack>
        {numberOfPages > 0 && (
          <Box w="90%">
            <PageChanger
              page={page}
              setPage={setPage}
              numberOfPages={numberOfPages}
            />
          </Box>
        )}
      </VStack>
    </>
  );
};

export default BlogPosts;
