import { Box, Text, VStack } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import { useLazyGetTopTopicsQuery } from "../../../store/api/forum-api-slice";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { Page } from "../../../store/api/result/Page";
import { SlideType } from "../../Carousel/Slide";
import { topicHeaderFontSize } from "../common/ForumDimensions";
import {
  slideTextFontSize,
  topicCarouselHeight,
  topicCarouselMarginX,
  topicHeight,
  topicWidth,
} from "../common/ForumDimensions";
import TopCategories from "../../Carousel/TopCategories";
import CategoriesCarousel from "../../Carousel/CategoriesCarousel";
import { useSelector } from "react-redux";
import { RootState } from "../../../store";

type PopularTopicsProps = {
  width: number;
};

const PopularTopics = ({ width }: PopularTopicsProps) => {
  const [popularTopics, setPopularTopics] = useState<SlideType[]>([]);
  const [fetchPopularTopics] = useLazyGetTopTopicsQuery();
  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    if (initialized && keycloak.authenticated) {
      fetchPopularTopics({ page: 0, count: 7 }).then((result) => {
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
        setPopularTopics(topics);
      });
    }
  }, [fetchPopularTopics, initialized, keycloak.authenticated]);

  const alternativeVersionOfInterface = useSelector(
    (state: RootState) => state.auth.alternativeVersionOfInterface
  );

  return (
    <VStack width={width}>
      <Text color="primary.300" fontSize={topicHeaderFontSize} textStyle="h1">
        Popular Topics
      </Text>
      <Box width="100%">
        {!alternativeVersionOfInterface ? (
          <CategoriesCarousel
            categories={popularTopics}
            slideWidth={topicWidth}
            slideHeight={topicHeight}
            slideMargin={topicCarouselMarginX}
            carouselHeight={topicCarouselHeight}
            slideTextFontSize={slideTextFontSize}
            otherComponentsWidth={window.innerWidth - width}
            isTextVisible
          />
        ) : (
          <TopCategories
            categories={popularTopics}
            slideWidth={topicWidth}
            slideHeight={topicHeight}
            slideMargin={topicCarouselMarginX}
            carouselHeight={topicCarouselHeight}
            slideTextFontSize={slideTextFontSize}
            otherComponentsWidth={window.innerWidth - width}
            isTextVisible
          />
        )}
      </Box>
    </VStack>
  );
};

export default PopularTopics;
