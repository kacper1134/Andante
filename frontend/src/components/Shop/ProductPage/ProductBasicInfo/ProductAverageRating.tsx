import { HStack, Text, useToken } from "@chakra-ui/react";
import { Rating } from "react-simple-star-rating";
import { ProductOutputDTO } from "../../../../store/api/result/dto/product/base/ProductOutputDTO";
import { scroller } from "react-scroll";
import { Dispatch, SetStateAction } from "react";

type ProductAverageRatingProps = {
  product: ProductOutputDTO;
  starSize: number;
  fontSize: {
    base: string;
    sm: string;
    md: string;
    lg: string;
    xl: string;
    "2xl": string;
  };
  areFeaturesOpen: boolean;
  setAreFeaturesOpen: Dispatch<SetStateAction<boolean>>;
};

const ProductAverageRating = ({product, starSize, fontSize, areFeaturesOpen, setAreFeaturesOpen} : ProductAverageRatingProps) => {
  const [secondary400] = useToken("colors", ["secondary.400"]);
  return (
    <HStack
      pt="1%"
      cursor="pointer"
      onClick={() => {
        setAreFeaturesOpen(false);
        setTimeout(() => {
          scroller.scrollTo("shop-comments", {
            duration: 600,
            smooth: true,
          });
        }, areFeaturesOpen ? 500 : 0);
      }}
    >
      <Rating
        initialValue={product.averageRating}
        fillColor={secondary400}
        size={starSize}
        SVGstorkeWidth={1.5}
        allowFraction
        readonly
      />
      <Text fontSize={fontSize} color="gray" pt={{ base: "4px", lg: "0px" }} textStyle="p">
        {product.comments.length}
      </Text>
    </HStack>
  );
};

export default ProductAverageRating;
