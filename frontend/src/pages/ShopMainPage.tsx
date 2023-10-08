import { useBreakpointValue, VStack, Flex, useToast } from "@chakra-ui/react";
import ProducentsCarousel from "../components/Shop/MainPage/ProducentCarousel/ProducentsCarousel";
import FilterMenu from "../components/Shop/MainPage/FilterMenu/FilterMenu";
import RecentlyAddedProducts from "../components/Shop/MainPage/Products/RecentlyAddedProducts";
import { Route, Routes, useSearchParams } from "react-router-dom";
import { Fragment, useEffect } from "react";
import ShopProductPage from "./ShopProductPage";
import HighestRatingProducts from "../components/Shop/MainPage/Products/HighestRatingProducts";

const ShopMainPage = () => {
  const filterMenuWidth = useBreakpointValue({
    base: "180px",
    sm: "200px",
    md: "220px",
    lg: "260px",
    xl: "300px",
  });

  const headerSize = useBreakpointValue({
    base: "20px",
    sm: "25px",
    md: "30px",
    lg: "35px",
    xl: "40px",
  });

  const toast = useToast();
  const [searchParams] = useSearchParams();
  const payment_intent = searchParams.get("payment_intent");
  const redirect_status = searchParams.get("redirect_status");

  useEffect(() => {
    if (payment_intent && redirect_status) {
      if (redirect_status === "succeeded") {
        toast({
          title: "Success",
          description: "Transaction Completed Successfully",
          status: "success",
          isClosable: true,
        });
      } else {
        toast({
          title: "Something went wrong",
          description: "Transaction have finished unsuccessfully. Order has not been placed!",
          status: "error",
          isClosable: true,
        });
      }
    }
  }, [payment_intent, redirect_status, toast]);

  return (
    <Routes>
      <Route
        path="/"
        element={
          <Fragment>
            <ProducentsCarousel />
            <Flex position="relative">
              <FilterMenu
                width={filterMenuWidth!}
                hasBoxShadow
                shouldFillAvailableSpace
              />
              <VStack width="100%">
                <RecentlyAddedProducts
                  filterMenuWidth={filterMenuWidth!}
                  headerSize={headerSize!}
                />
                <HighestRatingProducts
                  filterMenuWidth={filterMenuWidth!}
                  headerSize={headerSize!}
                />  
              </VStack>
            </Flex>
          </Fragment>
        }
      />
      <Route path="product/:id" element={<ShopProductPage />} />
    </Routes>
  );
};

export default ShopMainPage;
