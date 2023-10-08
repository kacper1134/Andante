import { VStack } from "@chakra-ui/react";
import AllTopics from "../components/Forum/Home/AllTopics";
import PopularTopics from "../components/Forum/Home/PopularTopics";
import RecentTopics from "../components/Forum/Home/RecentTopics";
import { topicContentMarginY } from "../components/Forum/common/ForumDimensions";
import { Route, Routes } from "react-router-dom";
import ForumTopicPage from "./ForumPostsPage";
import useMeasure from "react-use-measure";
import useAuthentication from "../hooks/useAuthentication";
import ForumPostPage from "./ForumPostPage";

const ForumHomePage = () => {
  const [ref, bounds] = useMeasure();
  useAuthentication("/forum");
  return (
    <Routes>
      <Route
        path="/"
        element={
          <VStack
            my={topicContentMarginY}
            spacing={topicContentMarginY}
            width="100%"
          >
            <PopularTopics width={bounds.width} />
            <RecentTopics width={bounds.width} />
            <AllTopics ref={ref} />
          </VStack>
        }
      />
      <Route path="topic/:id/*" element={<ForumTopicPage />} />
      <Route
        path="post/:id"
        element={
          <VStack mb="1%">
            <ForumPostPage />
          </VStack>
        }
      />
    </Routes>
  );
};

export default ForumHomePage;
