import { Box, Center, Text, useToast } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { useLazyGetBestRatedProductsQuery } from "../../../../store/api/productSlice";
import { isProductOutputDTOArray, ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import ProductsCarousel from "./ProductsCarousel";

type HighestRatingProductsProps = {
  filterMenuWidth: string;
  headerSize: string;
};

const HighestRatingProducts = ({
  filterMenuWidth,
  headerSize,
}: HighestRatingProductsProps) => {
  const [highestRatingProducts, setHighestRatingProducts] = useState<ProductOutputDTO[]>([]);
  const [errorMessages, setErrorMessages] = useState<string[]>([]);
  const toast = useToast();
  const [ getByQueryTrigger ] = useLazyGetBestRatedProductsQuery();
  const generalErrorMessage = "Could not fetch highest rated products from our service"

  useEffect(() => {
    const fetchProducts = async () => {
      const request = {
        page: 0,
        size: 8,
      };

      const response = await getByQueryTrigger(request);

      if (!response.data) {
        setErrorMessages([generalErrorMessage]);
      } else if (isProductOutputDTOArray(response.data)) {
        setHighestRatingProducts(response.data);
      } else {
        setErrorMessages(response.data);
      }
    }

    fetchProducts().catch(() => setErrorMessages([generalErrorMessage]));
  }, [getByQueryTrigger]);

  useEffect(() => {
    errorMessages.forEach(message => toast({
        title: 'Something went wrong',
        description: message,
        status: 'error',
        duration: 9000,
        isClosable: true, 
    }));   
 }, [errorMessages, toast]);

  return (
    <Box width="100%" pt="15px" overflow="hidden">
      <Center>
        <Text color="primary.500" fontWeight="semibold" fontSize={headerSize} textStyle="h1">
          HIGHEST RATING
        </Text>
      </Center>
      <ProductsCarousel products={highestRatingProducts} filterMenuWidth={filterMenuWidth} />
    </Box>
  );
};

export default HighestRatingProducts;