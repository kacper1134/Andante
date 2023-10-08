import { ChevronRightIcon } from "@chakra-ui/icons";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  Text,
  VStack,
} from "@chakra-ui/react";
import { useKeycloak } from "@react-keycloak/web";
import { useEffect, useState } from "react";
import { NavLink, useParams } from "react-router-dom";
import {
  useLazyGetTopicQuery,
  useLazyGetTopicsHierarchyQuery,
} from "../../../store/api/forum-api-slice";
import { TopicOutputDTO } from "../../../store/api/result/dto/forum/TopicOutputDTO";
import {
  buttonTextFontSize,
  topicHeaderFontSize,
} from "../common/ForumDimensions";
import SubTopicCarousel from "./SubTopicCarousel";

type NavigationItem = {
  text: string;
  link: string;
};

const PostsPageHeader = () => {
  const { keycloak, initialized } = useKeycloak();
  const { id } = useParams();
  const [topic, setTopic] = useState<TopicOutputDTO>();
  const [navigationItems, setNavigationItems] = useState<NavigationItem[]>([]);
  const [fetchSubtopics] = useLazyGetTopicQuery();
  const [fetchHierarchy] = useLazyGetTopicsHierarchyQuery();

  useEffect(() => {
    if (initialized && keycloak.authenticated && id !== undefined) {
      fetchSubtopics({ topicId: +id }).then((result) => {
        setTopic(result.data as TopicOutputDTO);
      });
      fetchHierarchy({ topicId: +id }).then((result) => {
        const topics = result.data as TopicOutputDTO[];
        const navigationItemsData = [{ text: "Home", link: "../" }];
        setNavigationItems(
          navigationItemsData.concat(
            topics.map((topic) => {
              return { text: topic.name, link: `../topic/${topic.id}` };
            })
          )
        );
      });
    }
  }, [fetchHierarchy, fetchSubtopics, id, initialized, keycloak.authenticated]);

  return (
    <VStack mt="1%" w="80%" alignItems="flex-start">
      <Breadcrumb
        spacing="8px"
        fontSize={buttonTextFontSize}
        separator={<ChevronRightIcon color="gray.500" />}
      >
        {navigationItems.map((breadcrumb, index) => (
          <BreadcrumbItem key={index} textStyle="p">
            <BreadcrumbLink
              as={NavLink}
              to={breadcrumb.link}
              _activeLink={{ color: "purple", fontWeight: "bold" }}
              _hover={{ fontStyle: "none" }}
              end
            >
              {breadcrumb.text}
            </BreadcrumbLink>
          </BreadcrumbItem>
        ))}
      </Breadcrumb>
      <Text color="primary.400" fontSize={topicHeaderFontSize} textStyle="h1">
        {topic?.name}
      </Text>
      <SubTopicCarousel />
    </VStack>
  );
};

export default PostsPageHeader;
