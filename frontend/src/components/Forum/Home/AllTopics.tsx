import { SlideType } from "../../Carousel/Slide";
import { Button, SimpleGrid, VStack, Text } from "@chakra-ui/react";
import Topic from "../common/Topic";
import { useEffect, useState } from "react";
import { AnimatePresence } from "framer-motion";
import LazyLoadingList from "../common/LazyLoadingList";
import {
  buttonTextFontSize,
  topicHeaderFontSize,
} from "../common/ForumDimensions";
import TopicsFilterAndSort from "./TopicsFilterAndSort";
import React from "react";
import {
  TopicQuerySpecification,
  TopicSortingOrder,
  useLazyGetTopicByQueryQuery,
} from "../../../store/api/forum-api-slice";
import { useKeycloak } from "@react-keycloak/web";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { Page } from "../../../store/api/result/Page";

const PAGE_AMOUNT = 4;

const AllTopics = React.forwardRef<HTMLDivElement>((_, ref) => {
  const [topics, setTopics] = useState<SlideType[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [numberOfTopics, setNumberOfTopics] = useState(0);
  const [fetchTopics] = useLazyGetTopicByQueryQuery();
  const { keycloak, initialized } = useKeycloak();
  const [isBottom, setIsBottom] = useState(false);
  const [sortingOrder, setSortingOrder] = useState(
    TopicSortingOrder.NEWEST_FIRST
  );
  const [searchPhrase, setSearchPhrase] = useState("");
  const [disableSortFilter, setDisableSortFilter] = useState(false);

  const fetchMoreData = (
    setIsBottom: React.Dispatch<React.SetStateAction<boolean>>
  ) => {
    setDisableSortFilter(true);
    const query: TopicQuerySpecification = {
      query: `parentTopic==null;name==${searchPhrase}*`,
      pageNumber: currentPage + 1,
      pageAmount: PAGE_AMOUNT,
      sortingOrder: sortingOrder,
    };

    fetchTopics({ query }).then((result) => {
      const page = result.data as Page<TopicOutputDTO>;
      const topics = page.content.map((topic) => {
        return {
          id: topic.id,
          text: topic.name,
          image: topic.imageUrl,
          path: "topic",
        };
      });
      setTopics((prevTopics) => prevTopics.concat(topics));
      setDisableSortFilter(false);
    });

    setCurrentPage((prevCurrentPage) => prevCurrentPage + 1);
    setIsBottom(false);
  };

  const [showMore, setShowMore] = useState(false);

  useEffect(() => {
    if (initialized && keycloak.authenticated) {
      const query: TopicQuerySpecification = {
        query: `parentTopic==null;name==${searchPhrase}*`,
        pageNumber: 0,
        pageAmount: PAGE_AMOUNT * (currentPage + 1),
        sortingOrder: sortingOrder,
      };

      fetchTopics({ query }).then((result) => {
        const page = result.data as Page<TopicOutputDTO>;
        const topics = page.content.map((topic) => {
          return {
            id: topic.id,
            text: topic.name,
            image: topic.imageUrl,
            path: "topic",
          };
        });
        setTopics(topics);
        setNumberOfTopics(page.totalElements);
      });
    }
  }, [fetchTopics, initialized, keycloak.authenticated, sortingOrder, searchPhrase]);

  return (
    <VStack ref={ref} width="fit-content">
      <Text
        color="primary.300"
        fontSize={topicHeaderFontSize}
        mb="3%"
        textStyle="h1"
      >
        All topics
      </Text>
      <TopicsFilterAndSort
        setSortingOrder={setSortingOrder}
        setSearchPhrase={setSearchPhrase}
        disableSortFilter={disableSortFilter}
      />
      <LazyLoadingList
        list={topics}
        showMore={showMore}
        maximumListLength={numberOfTopics}
        fetchMoreData={fetchMoreData}
        isBottom={isBottom}
        setIsBottom={setIsBottom}
      >
        <SimpleGrid
          columns={{
            base: 2,
            md: 3,
            xl: 4,
          }}
          spacing={4}
        >
          {topics.map((topic, index) => (
            <AnimatePresence key={index}>
              <Topic topic={topic} />
            </AnimatePresence>
          ))}
        </SimpleGrid>
      </LazyLoadingList>
      {!showMore && (
        <VStack width="100%">
          <Text fontSize={buttonTextFontSize} fontWeight="bold" textStyle="p">
            Showing {topics.length} of {numberOfTopics}
          </Text>
          <Button
            fontSize={buttonTextFontSize}
            height="fit-content"
            py="8px"
            colorScheme="purple"
            fontWeight="bold"
            px="60px"
            textStyle="p"
            onClick={() => setShowMore(true)}
          >
            Show More
          </Button>
        </VStack>
      )}
    </VStack>
  );
});

export default AllTopics;
