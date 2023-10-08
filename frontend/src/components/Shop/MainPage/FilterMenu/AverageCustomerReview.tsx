import {
  Flex,
  HStack,
  Text,
  useBreakpointValue,
  useToken,
} from "@chakra-ui/react";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { Rating } from "react-simple-star-rating";
import { innerActions } from "../../../../store/inner/innerSlice";

const fontSize = {
  base: "13px",
  sm: "15px",
  md: "16px",
  lg: "19px",
  xl: "21px",
};

type AverageCustomerReviewProps = {
  rating: number;
  setRating: React.Dispatch<React.SetStateAction<number>>;
};

const AverageCustomerReview = ({
  rating,
  setRating,
}: AverageCustomerReviewProps) => {
  const dispatch = useDispatch();
  const handleRating = (rate: number) => {
    if (rate === rating) setRating(0);
    else setRating(rate);
  };
  const [primary500] = useToken("colors", ["primary.500"]);
  const ratingSize = useBreakpointValue({
    base: 18,
    sm: 20,
    md: 22,
    lg: 25,
    xl: 28,
  });

  useEffect(() => {
    dispatch(innerActions.setRatingState(rating));
  }, [rating, dispatch]);

  return (
    <Flex direction="column" p="5%" fontSize={fontSize}>
      <Text fontWeight="bold" textStyle="h1">Average customer review</Text>
      <HStack>
        <Rating
          key={rating}
          size={ratingSize}
          style={{ letterSpacing: 20 }}
          initialValue={rating}
          onClick={handleRating}
          fillColor={primary500}
          emptyColor="white"
          SVGstrokeColor={primary500}
          SVGstorkeWidth={1.5}
          allowFraction
          transition
        />
        <Text fontWeight="bold" pt={1} textStyle="h1">& Up</Text>
      </HStack>
    </Flex>
  );
};

export default AverageCustomerReview;
