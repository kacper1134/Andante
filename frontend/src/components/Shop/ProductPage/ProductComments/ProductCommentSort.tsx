import { Select, useToken } from "@chakra-ui/react";
import { Dispatch, SetStateAction } from "react";
import { CommentSortingOrder } from "../../../../store/api/productSlice";

const options = [
  { value: CommentSortingOrder.NEWEST_FIRST, text: "Newest First" },
  { value: CommentSortingOrder.OLDEST_FIRST, text: "Oldest First" },
  { value: CommentSortingOrder.HIGHEST_RATING, text: "Rating: Highest First" },
  { value: CommentSortingOrder.LOWEST_RATING, text: "Rating: Lowest First" },
];

export interface ProductCommentSortProps {
  sortingOrder: string,
  setSortingOrder: Dispatch<SetStateAction<string>>,
}

const ProductCommentSort: React.FC<ProductCommentSortProps> = ({sortingOrder, setSortingOrder}) => {
  const optionStyle = { fontWeight: "bold" };
  const color = useToken("colors", "primary.400");
  return (
      <Select
        variant="filled"
        width="fit-content"
        bg="white"
        alignSelf="start"
        _focus={{ backgroundColor: "white" }}
        fontWeight="bold"
        cursor="pointer"
        color={color}
        pt="16px"
        fontSize={{
          base: "10px",
          sm: "12px",
          md: "14px",
          lg: "16px",
          xl: "18px",
        }}
        textStyle="p"
        value={sortingOrder}
        onChange={(event) => setSortingOrder(event.target.value)}
      >
        {options.map((option, index) => (
          <option key={index} value={option.value} style={optionStyle}>
            {option.text}
          </option>
        ))}
      </Select>
  );
};

export default ProductCommentSort;
