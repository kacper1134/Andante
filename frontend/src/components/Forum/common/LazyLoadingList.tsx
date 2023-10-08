import { Spinner, VStack } from "@chakra-ui/react";
import { AnimatePresence } from "framer-motion";
import { useEffect } from "react";
import InfiniteScroll from "react-infinite-scroll-component";

type LazyLoadingListProps<Type> = {
  children: React.ReactNode;
  list: Type[];
  fetchMoreData: (
    setIsBottom: React.Dispatch<React.SetStateAction<boolean>>
  ) => void;
  showMore: boolean;
  maximumListLength: number;
  isBottom: boolean;
  setIsBottom: React.Dispatch<React.SetStateAction<boolean>>;
};

const LazyLoadingList = <Type extends unknown>({
  children,
  list,
  showMore,
  maximumListLength,
  fetchMoreData,
  isBottom,
  setIsBottom,
}: LazyLoadingListProps<Type>) => {
  useEffect(() => {
    const onScroll = () => {
      const isBottom =
        Math.ceil(window.innerHeight + window.scrollY) >=
        document.documentElement.scrollHeight;
      if (isBottom) setIsBottom(true);
    };
    document.addEventListener("scroll", onScroll);
    return () => document.removeEventListener("scroll", onScroll);
  }, [setIsBottom]);

  return (
    <InfiniteScroll
      dataLength={list.length}
      next={() => fetchMoreData(setIsBottom)}
      hasMore={isBottom && list.length < maximumListLength && showMore}
      loader={
        <VStack mt="10px" width="100%" alignItems="center">
          <Spinner color="primary.200" size={{ base: "md", md: "xl" }} />
        </VStack>
      }
      style={{ overflow: "hidden" }}
    >
      <AnimatePresence>{children}</AnimatePresence>
    </InfiniteScroll>
  );
};

export default LazyLoadingList;
