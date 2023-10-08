import { Box, Text, useBreakpointValue, useToken } from "@chakra-ui/react";
import { Rating } from "react-simple-star-rating";
import { CommentOutputDTO } from "../../../../store/api/result/dto/product/CommentOutputDTO";
import parser from "html-react-parser";
import NewProductCommentForm from "./NewProductComment/NewProductCommentForm";

const headerSize = {
  base: "11px",
  sm: "14px",
  md: "16px",
  lg: "20px",
  xl: "24px",
};

const fontSize = {
  base: "10px",
  sm: "12px",
  md: "14px",
  lg: "16px",
  xl: "18px",
};

type ProductCommentContentProps = {
  comment: CommentOutputDTO;
  isEdit: boolean;
  setIsEdit: React.Dispatch<React.SetStateAction<boolean>>;
};

const ProductCommentContent = ({
  comment,
  isEdit,
  setIsEdit,
}: ProductCommentContentProps) => {
  const [secondary400] = useToken("colors", ["secondary.400"]);
  const starSize = useBreakpointValue({
    base: "12px",
    sm: "14px",
    md: "16px",
    lg: "18px",
    xl: "20px",
  });

  return (
    <>
      <NewProductCommentForm
        isOpen={isEdit}
        isEdit={true}
        setIsOpen={setIsEdit}
        productId={comment.productId}
        currentRating={comment.rating}
        currentTitle={comment.title}
        currentContent={comment.content}
        observers={comment.observers}
        commentId={comment.id}
      />
      {!isEdit && (
        <>
          <Box width="95%">
            <Rating
              initialValue={comment.rating}
              fillColor={secondary400}
              size={parseInt(starSize!)}
              SVGstorkeWidth={1.5}
              allowFraction
              readonly
            />
          </Box>
          <Text width="95%" fontSize={headerSize} textStyle="h1">
            {comment.title}
          </Text>
          <Box fontSize={fontSize} width="95%" textStyle="p">
            {parser(comment.content)}
          </Box>
        </>
      )}
    </>
  );
};

export default ProductCommentContent;
