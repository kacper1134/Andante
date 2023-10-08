import { Box } from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useLazyGetSubtopicsQuery } from "../../../store/api/forum-api-slice";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import { SlideType } from "../../Carousel/Slide";
import TopicsCarousel from "../common/TopicsCarousel";

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

  return (
    <>
      {(topics && topics.length > 0) && (
        <Box width="100%">
          <TopicsCarousel
            topics={topics}
            otherComponentsWidth={window.innerWidth * 0.2}
          />
        </Box>
      )}
    </>
  );
};

export default SubTopicCarousel;
