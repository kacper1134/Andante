import { Route, Routes } from "react-router-dom";
import BlogCreatePage from "../components/Blog/BlogCreatePage/BlogCreatePage";
import BlogPosts from "../components/Blog/BlogMainPage/BlogPosts";
import BlogPostDetails from "../components/Blog/BlogPostPage/BlogPostDetails";
import useAuthentication from "../hooks/useAuthentication";

type BlogMainPageProps = {
  topic: "music" | "reviews" | "recommended";
};

const BlogMainPage = ({ topic }: BlogMainPageProps) => {
  useAuthentication("/" + topic);
  return (
    <Routes>
      <Route path="/" element={<BlogPosts topic={topic} />} />
      <Route path="/create" element={<BlogCreatePage topic={topic} mode="create" />} />
      <Route path="/edit/:slug" element={<BlogCreatePage topic={topic} mode="edit" />} />
      <Route path="/:slug" element={<BlogPostDetails />} />
    </Routes>
  );
};

export default BlogMainPage;
