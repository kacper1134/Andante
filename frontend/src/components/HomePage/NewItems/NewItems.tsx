import { Text, SimpleGrid, VStack } from "@chakra-ui/react";
import { ProductSortingOrder } from "../../../store/api/productSlice";
import { Page } from "../../../store/api/result/Page";
import { ProductOutputDTO, useGetByQuery } from "../../../store/api/result/dto/product/base/ProductOutputDTO";
import NewItem from "./NewItem";
import { useMemo } from "react";

export function isProductPage(value: Page<ProductOutputDTO> | string[]): value is Page<ProductOutputDTO> {
  return "content" in value;
}

const NewItems = () => {
 const productPage = useGetByQuery(useMemo(() => { return {
  query: "id=gt=0",
  pageNumber: 0,
  pageSize: 4,
  sortingOrder: ProductSortingOrder.RECENTLY_ADDED
 }}, []), 0);

  return (
    <VStack
      bgImage="url(/newArrivalsBackground.jpg)"
      bgRepeat="no-repeat"
      bgSize="cover"
      bgPosition="center"
      minH="400px"
    >
      <Text
        pt="8"
        color="primary.200"
        fontSize={{ base: "3xl", sm: "5xl" }}
        textStyle="h1"
      >
        NEW ARRIVALS
      </Text>
      <SimpleGrid
        columns={{ base: 1, md: 2, lg: 3, xl: 4 }}
        spacing={{ md: "15", xl: "10" }}
      >
        {productPage.content.map((product) => (
          <NewItem key={product.id} product={product} />
        ))}
      </SimpleGrid>
    </VStack>
  );
};

export default NewItems;
