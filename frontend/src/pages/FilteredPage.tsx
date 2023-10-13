import { useBreakpointValue, VStack, Flex, Spacer, Center } from "@chakra-ui/react";
import FilterMenu from "../components/Shop/MainPage/FilterMenu/FilterMenu";
import ResultsBar from "../components/Shop/ResultsPage/ResultsBar";
import Results from "../components/Shop/ResultsPage/Results";
import PageSelector from "../components/Shop/ResultsPage/PageSelector";
import { useState } from "react";
import { ProductSortingOrder, useGetByFilterState } from "../store/api/productSlice";

export interface ShopMainPageProps {
  menuHeight: string,
}

const ShopMainPage: React.FC<ShopMainPageProps> = ({menuHeight}) => {
  const [currentPage, setCurrentPage] = useState(0);
  const [sortingOrder, setSortingOrder] = useState(ProductSortingOrder.ALPHABETICAL);
  const result = useGetByFilterState(currentPage, 20, sortingOrder);
  const offset = result.pageable.offset;
  const resultRange = `${offset + 1} to ${offset + result.numberOfElements}`;
  const totalCount = result.totalElements;
  const isMobile = useBreakpointValue({
    base: true,
    sm: false,
  });

  const filterMenuWidth = useBreakpointValue({
    base: "200px",
    sm: "225px",
    md: "250px",
    lg: "300px",
    xl: "350px",
  });

  return (
    <>
      <ResultsBar setSortingOrder={setSortingOrder} resultRange={resultRange} totalCount={totalCount} />
      <Flex position="relative">
        {!isMobile && <FilterMenu width={filterMenuWidth!} hasBoxShadow={false} shouldFillAvailableSpace={false} menuHeight={menuHeight} />}
        <VStack w="100%" alignItems="start" pl="32px">
          <Results products={result.content} />
          <Spacer />
          <Center w="100%">
            <PageSelector currentPage={currentPage} setCurrentPage={setCurrentPage} totalNumberOfPages={result.totalPages} />
          </Center>
        </VStack>
      </Flex>
    </>
  );
};

export default ShopMainPage;