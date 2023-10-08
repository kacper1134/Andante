import { VStack } from "@chakra-ui/react";
import { Route, Routes } from "react-router-dom";
import Posts from "../components/Forum/Posts/Posts";
import PostsPageHeader from "../components/Forum/Posts/PostsPageHeader";

const ForumPostsPage = () => {
  return (
    <VStack mb="1%">
      <Routes>
        <Route
          path="/"
          element={
            <>
              <PostsPageHeader />
              <Posts />
            </>
          }
        />
      </Routes>
    </VStack>
  );
};

export default ForumPostsPage;
