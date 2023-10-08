import {
  Box,
  Flex,
  Grid,
  GridItem,
  Image,
  Spacer,
  Text,
  useBreakpointValue,
} from "@chakra-ui/react";
import { Link } from "react-router-dom";
import { urlFor } from "../../../client";
import {
  postAuthorFontSize,
  postDescriptionFontSize,
  postTitleMainPageFontSize,
} from "./BlogMainPageSizes";
import parse from "html-react-parser";
import BlogPostOptions from "./BlogPostOptions";
import { useKeycloak } from "@react-keycloak/web";
import { KeycloakRole } from "../../../enums/KeycloakRole";

type BlogPostProps = {
  post: any;
  setUpdated: React.Dispatch<React.SetStateAction<boolean>>;
};

const weekday = [
  "Sunday",
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
];

const BlogPost = ({ post, setUpdated }: BlogPostProps) => {
  const publishDay = new Date(post.publishedAt);
  const photoVisible = useBreakpointValue({ base: false, md: true });
  const { keycloak } = useKeycloak();

  return (
    <Box
      position="relative"
      borderRadius={"8px"}
      margin="16px"
      p="16px"
      boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
      w="90%"
    >
      <Grid
        templateRows="repeat(3, 1fr)"
        templateColumns="repeat(10, 1fr)"
        gap="8px"
      >
        {post.image && photoVisible && (
          <GridItem rowSpan={3} colSpan={2}>
            <Link to={post.slug?.current}>
              <Image
                src={urlFor(post.image).width(300).url()}
                h="200px"
                w="300px"
                objectFit="contain"
              />
            </Link>
          </GridItem>
        )}
        <GridItem rowSpan={2} colSpan={photoVisible ? 8 : 10}>
          <Link to={post.slug?.current}>
            <Text
              color="primary.500"
              fontWeight="bold"
              fontSize={postTitleMainPageFontSize}
              textStyle="h2"
              m="4px"
              p="4px"
            >
              {post.title}
            </Text>
          </Link>
          <Box
            fontSize={postDescriptionFontSize}
            color="blackAlpha.700"
            noOfLines={[1, 2, 3]}
            textStyle="p"
            m="4px"
            p="4px"
          >
            {parse(post.description)}
          </Box>
        </GridItem>
        <GridItem
          rowSpan={1}
          colSpan={photoVisible ? 8 : 10}
          textStyle="p"
          position="relative"
        >
          <Flex
            align="center"
            p="4px"
            fontSize={postAuthorFontSize}
            position="absolute"
            bottom="0"
            w="100%"
          >
            <Text ml="6px" mr="6px" color="gray">
              By
            </Text>
            <Text color="primary.500" fontWeight="bold" mr="6px">
              {post.author}
            </Text>
            <Text color="gray">
              on {weekday[publishDay.getDay()]}, {publishDay.getDate()}{" "}
              {publishDay.toLocaleString("default", { month: "long" })}{" "}
              {publishDay.getFullYear()} at{" "}
              {publishDay.getHours().toString().padStart(2, "0")}:
              {publishDay.getMinutes().toString().padStart(2, "0")}
            </Text>
            <Spacer />
            {keycloak.hasRealmRole(KeycloakRole.BLOGGER) && (
              <BlogPostOptions
                title={post.title}
                id={post._id}
                image={post.image}
                setUpdated={setUpdated}
                slug={post.slug}
              />
            )}
          </Flex>
        </GridItem>
      </Grid>
    </Box>
  );
};

export default BlogPost;
