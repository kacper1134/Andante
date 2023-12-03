import { VStack, Text } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  PostQuerySpecification,
  TopicSortingOrder,
  useLazyGetPostQuery,
  useLazyGetPostResponseByQueryQuery,
} from "../../../store/api/forum-api-slice";
import { useLazyGetUsersImageQuery } from "../../../store/api/profile-api-slice";
import { PostOutputDTO } from "../../../store/api/result/dto/forum/PostOutputDTO";
import { PostResponseOutputDTO } from "../../../store/api/result/dto/forum/PostResponseOutputDTO";
import { Page } from "../../../store/api/result/Page";
import PageChanger from "../../Shop/ProductPage/ProductComments/PageChanger";
import { postTitleFontSize, replySpacing } from "../common/ForumDimensions";
import { formatDate } from "../Posts/Post";
import NewPostReply from "./NewPostReply";

import Post, { PostType } from "./Post";
import Reply, { ReplyType } from "./Reply";

const REPLIES_PER_PAGE = 5;

const ForumPostPage = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [reloadReplies, setReloadReplies] = useState(true);
  const [reloadPost, setReloadPost] = useState(true);
  const [page, setPage] = useState(0);
  const [numberOfPages, setNumberOfPages] = useState(0);
  const [numberOfReplies, setNumberOfReplies] = useState(0);
  const [post, setPost] = useState<PostType>();
  const [replies, setReplies] = useState<ReplyType[]>();
  const { keycloak, initialized } = useKeycloak();
  const [getPost] = useLazyGetPostQuery();
  const [getImages] = useLazyGetUsersImageQuery();
  const [getPostReplies] = useLazyGetPostResponseByQueryQuery();
  const { id } = useParams();

  useEffect(() => {
    if (initialized && keycloak.authenticated && id && reloadPost) {
      getPost({ postId: +id })
        .then((result) => {
          if ("data" in result) {
            const postData = result.data as PostOutputDTO;
            getImages(postData.user.username).then((image) => {
              let profileImage = "";
              if (image && "data" in image && image.data?.length! > 0) {
                profileImage = (image.data![0] as any).imageUrl;
              }
              setPost({
                title: postData.title!,
                date: formatDate(postData.creationTimestamp!),
                category: postData.topic,
                image: profileImage,
                user: postData.user.username,
                content: postData.content,
                likes: postData.likes!,
                topicId: +postData.topic.id!,
                numberOfLikes: postData.likesAmount,
              });
            });
          }
        })
        .finally(() => setReloadPost(false));
    }
  }, [getImages, getPost, id, initialized, keycloak.authenticated, reloadPost]);

  useEffect(() => {
    if (initialized && keycloak.authenticated && id && reloadReplies) {
      const query: PostQuerySpecification = {
        query: `postId==${id}`,
        pageNumber: page,
        pageSize: REPLIES_PER_PAGE,
        sortingOrder: TopicSortingOrder.OLDEST_FIRST,
      };
      getPostReplies({ query: query })
        .then((result) => {
          const repliesData = result.data as Page<PostResponseOutputDTO>;
          const usernames = Array.from(
            new Set(repliesData.content?.map((reply) => reply.user.username))
          );
          usernames.length === 0
            ? Promise.resolve()
            : getImages(usernames.toString()).then((response) => {
                const images: any = response.data?.map((image) => [
                  image.username,
                  image.imageUrl,
                ]);
                const userImages = new Map<string, string>(images);
                setReplies(
                  repliesData.content?.map((reply) => {
                    const username = reply.user.username;
                    return {
                      id: reply.id,
                      date: formatDate(reply.creationTimestamp),
                      image: userImages.has(username)
                        ? userImages.get(username)!
                        : "",
                      user: username,
                      content: reply.content,
                      likes: reply.likes,
                      numberOfLikes: reply.likesAmount,
                    };
                  })
                );
              });
          setNumberOfPages(repliesData.totalPages);
          setNumberOfReplies(repliesData.totalElements);
        })
        .finally(() => setReloadReplies(false));
    }
  }, [
    getImages,
    getPostReplies,
    id,
    initialized,
    keycloak.authenticated,
    page,
    reloadReplies,
  ]);

  return (
    <VStack w="90%" mt="2%">
      {post && (
        <Post post={post} setIsOpen={setIsOpen} setReloadPost={setReloadPost} />
      )}
      <Text
        fontSize={postTitleFontSize}
        bg="primary.50"
        w="100%"
        p="1%"
        fontWeight="bold"
        textStyle="p"
      >
        {numberOfReplies} replies
      </Text>
      <NewPostReply
        isEdit={false}
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        setReloadReplies={setReloadReplies}
      />
      <VStack pt={replySpacing} spacing={replySpacing} w="100%">
        {replies &&
          replies?.map((reply, index) => (
            <Reply
              key={index}
              index={page * REPLIES_PER_PAGE + index + 1}
              reply={reply}
              setReloadReplies={setReloadReplies}
            />
          ))}
          
      <NewPostReply
        isEdit={false}
        isOpen={!isOpen}
        setIsOpen={setIsOpen}
        setReloadReplies={setReloadReplies}
      />
      </VStack>
      {replies && replies.length > 0 && (
        <PageChanger
          page={page}
          setPage={setPage}
          numberOfPages={numberOfPages}
          setReload={setReloadReplies}
        />
      )}
    </VStack>
  );
};

export default ForumPostPage;
