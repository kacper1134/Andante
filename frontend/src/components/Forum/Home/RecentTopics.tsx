import { Box, Text, VStack } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import {
  TopicQuerySpecification,
  TopicSortingOrder,
  useLazyGetTopicByQueryQuery,
} from "../../../store/api/forum-api-slice";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { Page } from "../../../store/api/result/Page";
import { SlideType } from "../../Carousel/Slide";
import { topicHeaderFontSize } from "../common/ForumDimensions";
import TopicsCarousel from "../common/TopicsCarousel";

type RecentTopicsProps = {
  width: number;
};

const RecentTopics = ({ width }: RecentTopicsProps) => {
  const [recentTopics, setRecentTopics] = useState<SlideType[]>([]);
  const [fetchRecentTopics] = useLazyGetTopicByQueryQuery();
  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    if (initialized && keycloak.authenticated) {
      const query: TopicQuerySpecification = {
        query: "parentTopic==null",
        pageNumber: 0,
        pageAmount: 7,
        sortingOrder: TopicSortingOrder.NEWEST_FIRST,
      };

      fetchRecentTopics({ query }).then((result) => {
        const topics = (result.data as Page<TopicOutputDTO>).content.map(
          (topic) => {
            return {
              id: topic.id,
              text: topic.name,
              image: topic.imageUrl,
              path: "topic",
            };
          }
        );
        setRecentTopics(topics);
      });
    }
  }, [fetchRecentTopics, initialized, keycloak.authenticated]);

  return (
    <VStack width={width}>
      <Text color="primary.300" fontSize={topicHeaderFontSize} textStyle="h1">
        Recent Topics
      </Text>
      <Box width="100%">
        <TopicsCarousel
          topics={recentTopics}
          otherComponentsWidth={window.innerWidth - width}
        />
      </Box>
    </VStack>
  );
};

export default RecentTopics;
