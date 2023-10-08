import { Box, Center, Text } from "@chakra-ui/react";
import { useMemo } from "react";
import { ProductSortingOrder } from "../../../../store/api/productSlice";
import { useGetByQuery } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import ProductsCarousel from "./ProductsCarousel";

type RecentlyAddedProductsProps = {
  filterMenuWidth: string;
  headerSize: string;
};

const RecentlyAddedProducts = ({
  filterMenuWidth,
  headerSize,
}: RecentlyAddedProductsProps) => {
  const productPage = useGetByQuery(useMemo(() => { return {
    query: "id=gt=0",
    pageNumber: 0,
    pageSize: 8,
    sortingOrder: ProductSortingOrder.RECENTLY_ADDED,
  }}, []), 0);

  return (
    <Box width="100%" pt="15px" overflow="hidden">
      <Center>
        <Text color="primary.500" fontWeight="semibold" fontSize={headerSize} textStyle="h1">
          RECENTLY ADDED
        </Text>
      </Center>
      <ProductsCarousel products={productPage.content} filterMenuWidth={filterMenuWidth} />
    </Box>
  );
};

export default RecentlyAddedProducts;
