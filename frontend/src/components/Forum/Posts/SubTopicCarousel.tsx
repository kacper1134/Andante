import { Box } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useLazyGetSubtopicsQuery } from "../../../store/api/forum-api-slice";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { SlideType } from "../../Carousel/Slide";
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
import AllTopics from "../Home/AllTopics";
import SubTopicList from "./SubTopicList";

const SubTopicCarousel = () => {
  const { id } = useParams();
  const [fetchSubtopics] = useLazyGetSubtopicsQuery();
  const { keycloak, initialized } = useKeycloak();
  const [topics, setTopics] = useState<SlideType[]>([]);

  useEffect(() => {
    if (initialized && keycloak.authenticated && id !== undefined) {
      fetchSubtopics({ topicId: +id }).then((result) => {
        if (result.data) {
          const subtopics = (result.data as TopicOutputDTO[]).map(
            (subtopic) => {
              return {
                id: subtopic.id,
                text: subtopic.name,
                image: subtopic.imageUrl,
                path: "../topic",
              };
            }
          );
          setTopics(subtopics);
        }
      });
    }
  }, [fetchSubtopics, id, initialized, keycloak.authenticated]);

  const alternativeVersionOfInterface = useSelector(
    (state: RootState) => state.auth.alternativeVersionOfInterface
  );

  return (
    <>
      {(topics && topics.length > 0) && (
        <Box width="100%">
          {!alternativeVersionOfInterface ? (
            <CategoriesCarousel
              categories={topics}
              slideWidth={topicWidth}
              slideHeight={topicHeight}
              slideMargin={topicCarouselMarginX}
              carouselHeight={topicCarouselHeight}
              slideTextFontSize={slideTextFontSize}
              otherComponentsWidth={window.innerWidth * 0.2}
              isTextVisible
            />
          ) : (
            <SubTopicList />
          )}
        </Box>
      )}
    </>
  );
};

export default SubTopicCarousel;
