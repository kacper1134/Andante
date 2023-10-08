import { useToast } from "@chakra-ui/react";
import { Fragment, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import ImageSlider from "../components/HomePage/ImageSlider/ImageSlider";
import NewItems from "../components/HomePage/NewItems/NewItems";
import Newsletter from "../components/HomePage/Newsletter/Newsletter";
import RecentBlogPosts from "../components/HomePage/RecentBlogPosts/RecentBlogPosts";
import { RootState } from "../store";
import { authActions } from "../store/auth/auth-slice";

const HomePage = () => {
  const errorMessages = useSelector((state: RootState) => state.inner.errorMessages);
  const shouldLogout = useSelector((state: RootState) => state.auth.shouldLogout);
  const toast = useToast();
  const dispatch = useDispatch();

  useEffect(() => {
    errorMessages.forEach(message => toast({
      title: "Something went wrong",
      description: message,
      status: "error",
      duration: 9000,
      isClosable: true,
    }))
  }, [errorMessages, toast]);

  useEffect(() => {
    if (shouldLogout) {
      dispatch(authActions.logout());
    }
  }, [shouldLogout, dispatch]);

  return (
    <Fragment>
      <ImageSlider />
      <RecentBlogPosts />
      <NewItems />
      <Newsletter />
    </Fragment>
  );
};

export default HomePage;
