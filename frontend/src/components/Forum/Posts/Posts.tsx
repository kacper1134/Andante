import { Text, HStack, VStack, Spinner } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import {
  TopicSortingOrder,
  useLazyGetPostByQueryQuery,
} from "../../../store/api/forum-api-slice";
import { PostOutputDTO } from "../../../store/api/result/dto/forum/PostOutputDTO";
import { Page } from "../../../store/api/result/Page";
import PageChanger from "../../Shop/ProductPage/ProductComments/PageChanger";
import NewPostButton from "./NewPost/NewPostButton";
import NewPostForm from "./NewPost/NewPostForm";
import Post, { PostType } from "./Post";
import PostsFilterAndSort from "./PostsFilterAndSort";

const Posts = () => {
  const { id } = useParams();
  const [isOpen, setIsOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [numberOfPages, setNumberOfPages] = useState(0);
  const [posts, setPosts] = useState<PostType[]>([]);

  const { keycloak, initialized } = useKeycloak();
  const [fetchPosts] = useLazyGetPostByQueryQuery();
  const [reload, setReload] = useState(false);

  const [sortingOrder, setSortingOrder] = useState(
    TopicSortingOrder.NEWEST_FIRST
  );
  const [searchPhrase, setSearchPhrase] = useState("");
  const location = useLocation();

  useEffect(() => {
    setReload(true);
    setIsOpen(false);
    setPage(0);
    setSortingOrder(TopicSortingOrder.NEWEST_FIRST);
    setSearchPhrase("");
  }, [location]);

  useEffect(() => {
    setPage(0);
  }, [searchPhrase]);

  useEffect(() => {
    if (initialized && keycloak.authenticated && reload) {
      const query = {
        query: `topicId==${id};title==${searchPhrase}*`,
        pageNumber: page,
        pageSize: 6,
        sortingOrder: sortingOrder,
      };
      fetchPosts({ query }).then((result) => {
        const page = result.data as Page<PostOutputDTO>;
        const fetchedPosts = page.content.map((post) => {
          return {
            id: post.id,
            author: post.user.username,
            date: post.creationTimestamp,
            title: post.title,
            numberOfComments: post.responsesAmount,
          };
        });
        setPosts(fetchedPosts);
        setNumberOfPages(page.totalPages);
        setReload(false);
      });
    }
  }, [
    fetchPosts,
    id,
    initialized,
    keycloak.authenticated,
    page,
    reload,
    searchPhrase,
    sortingOrder,
  ]);

  return (
    <VStack mt="1%" w="80%" alignItems="flex-start" spacing={10}>
      <VStack align="flex-start" w="100%">
        <HStack>
          <Text
            color="primary.400"
            fontSize={{
              base: "18px",
              sm: "22px",
              md: "26px",
              lg: "30px",
              xl: "34px",
            }}
            textStyle="h1"
          >
            Posts
          </Text>
          <NewPostButton setIsOpen={setIsOpen} />
        </HStack>
        <NewPostForm
          isOpen={isOpen}
          setReload={setReload}
          setIsOpen={setIsOpen}
        />
      </VStack>

      <PostsFilterAndSort
        setSortingOrder={setSortingOrder}
        setSearchPhrase={setSearchPhrase}
        setReload={setReload}
      />
      {posts.length > 0 &&
        posts.map((post, index) => <Post key={index} post={post} />)}
      {posts.length === 0 && reload === false && (
        <Text
          textStyle="h1"
          fontSize={{
            base: "10px",
            sm: "12px",
            md: "16px",
            lg: "20px",
            xl: "24px",
          }}
          alignSelf="center"
          color="primary.500"
        >
          There are no posts within this topic yet. Be the first person to write
          something!
        </Text>
      )}
      {posts.length === 0 && reload === true && <Spinner />}
      {posts.length !== 0 && (
        <PageChanger
          page={page}
          setPage={setPage}
          numberOfPages={numberOfPages}
          setReload={setReload}
        />
      )}
    </VStack>
  );
};

export default Posts;
