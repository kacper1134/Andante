import { VStack } from "@chakra-ui/react";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import { slideHeight, slideWidth } from "./ProductsCarouselDimensions";
import ProductSlideContent from "./ProductSlideContent";

export type ProductSlideType = {
  id: number;
  name: string;
  price: string;
  salePrice: string;
  url: string;
};

type ProductSlideProps = {
  product: ProductOutputDTO,
  isDragOn: boolean,
};

export const productMargin = 8;

const ProductSlide = ({ product, isDragOn }: ProductSlideProps) => {
  return (
    <VStack
      bgImage="url(/newArrivalsBackground.jpg)"
      bgRepeat="no-repeat"
      bgSize="cover"
      bgPosition="center"
      mx={`${productMargin}px!`}
      width={slideWidth}
      height={slideHeight}
    >
      <ProductSlideContent product={product} isDragOn={isDragOn} />
    </VStack>
  );
};

export default ProductSlide;
