import { useEffect, useState } from "react";
import {
  Box,
  Center,
  Flex,
  Progress,
  useToken,
  Text,
  VStack,
  HStack,
  Spacer,
  Spinner,
} from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import { readClient } from "../../../client.js";
import PostComments from "./PostComments/PostComments";
import { hexToRGB } from "../../../functions/style-functions";
import { quoteFontSize } from "../BlogMainPage/BlogMainPageSizes";
import { imageTextFontSize } from "./BlogPostPageSizes";
import PostContent from "./PostContent";
import { useLazyGetUsersImageQuery } from "../../../store/api/profile-api-slice";
import { useKeycloak } from "@react-keycloak/web";
import useGetFirebaseImage from "../../../hooks/useGetFirebaseImage";

const BlogPostDetails = () => {
  const [postData, setPostData] = useState<any>();
  const { keycloak, initialized } = useKeycloak();
  const [postCommentsData, setPostCommentsData] = useState<any[]>([]);
  const [color] = useToken("colors", ["primary.600"]);
  const backgroundColor = hexToRGB(color, 0.15);
  const { slug } = useParams();
  const [reloadComments, setReloadComments] = useState(true);
  const [userImages, setUserImages] = useState<Map<string, string>>();
  const [trigger] = useLazyGetUsersImageQuery();
  const [isPostDownload, setIsPostDownload] = useState(false);
  const [isCommentsDownload, setIsCommentsDownload] = useState(false);
  const getImage = useGetFirebaseImage();

  useEffect(() => {
    const postQuery = `*[_type == "post" && slug.current == "${slug}"]{
      title,
        description,
        body,
        image,
        "imageUrl": image.asset->url,
        author,
        publishedAt,
        slug
    }`;
    setIsPostDownload(true);
    readClient
      .fetch(postQuery, { slug })
      .then((post) => setPostData(post[0]))
      .catch(console.error)
      .finally(() => setIsPostDownload(false));
  }, [slug]);

  useEffect(() => {
    const commentsQuery = `*[_type == "comment" && slug == "${slug}"] | order(publishedAt desc){
    _id,
    author,
    username,
    publishedAt,
    content
  }`;
    if (reloadComments && initialized && keycloak.authenticated) {
      const getImageUrl = async (imagePath: string) => {
        return await getImage(imagePath).catch(() => "");
      };
      setIsCommentsDownload(true);
      readClient
        .fetch(commentsQuery, { slug })
        .then((comments) => {
          setPostCommentsData(comments);
          const usernames = Array.from(
            new Set(comments.map((comment: any) => comment.username))
          );
          if (usernames && usernames.length > 0) {
            trigger(usernames.toString()).then(async (response) => {
              const images: any = await Promise.all(
                response.data!.map(async (image) => [
                  image.username,
                  await getImageUrl(image.imageUrl),
                ])
              );

              const userImages: any = new Map(images);
              setUserImages(userImages);
            });
          }
        })
        .catch(console.error)
        .finally(() => setIsCommentsDownload(false));

      setReloadComments(false);
    }
  }, [
    slug,
    reloadComments,
    trigger,
    initialized,
    keycloak.authenticated,
    getImage,
  ]);

  if (!postData)
    return (
      <Box>
        <Progress size="xs" isIndeterminate color="primary.500" />
      </Box>
    );

  const publishDay = new Date(postData.publishedAt);

  return (
    <>
      <Box
        bgImage={`linear-gradient(${backgroundColor},${backgroundColor}), url(${postData.imageUrl})`}
        bgRepeat="no-repeat"
        bgSize="cover"
        bgPosition="center"
        minW="100%"
        h="80vh"
      >
        <Center as={VStack} h="100%" color="white">
          <Box>
            <Text textStyle="h1" fontSize={quoteFontSize}>
              {postData.title}
            </Text>
            <HStack fontSize={imageTextFontSize} textStyle="p">
              <Text>by {postData.author}</Text>
              <Spacer />
              <Text>
                {publishDay.getDate()}{" "}
                {publishDay.toLocaleString("default", { month: "long" })}{" "}
                {publishDay.getFullYear()}
              </Text>
              <Spacer />
              <Text>{postCommentsData.length} comments</Text>
            </HStack>
          </Box>
        </Center>
      </Box>
      <Flex alignItems="center" direction="column" mt="30px">
        {isPostDownload && <Spinner size="lg" color="primary.900" />}
        <PostContent content={postData.body} />
        <Box padding="16px" w="80%">
          <PostComments
            postCommentsData={postCommentsData}
            userImages={userImages}
            postTitle={postData.title}
            postSlug={postData.slug}
            setReloadComments={setReloadComments}
            isCommentsDownload={isCommentsDownload}
          />
        </Box>
      </Flex>
    </>
  );
};

export default BlogPostDetails;
