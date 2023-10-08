import { HStack, Icon } from "@chakra-ui/react";
import {
  MdStar,
  MdOutlineStarOutline,
  MdOutlineStarHalf,
} from "react-icons/md";

type StarRatingProps = {
  rating: number;
};

const StarRating = ({ rating }: StarRatingProps) => {
  const stars = [];
  let index = 0;
  const starColor = "primary.300";
  const starSize = {
    base: "20px",
    sm: "25px",
  };

  while (rating > 0) {
    if (rating !== 0.5) {
      stars.push(
        <Icon as={MdStar} color={starColor} key={index++} boxSize={starSize} />
      );
    } else {
      stars.push(
        <Icon
          as={MdOutlineStarHalf}
          color={starColor}
          key={index++}
          boxSize={starSize}
        />
      );
    }
    rating--;
  }
  const missingStars = 5 - stars.length;
  for (let i = 0; i < missingStars; i++) {
    stars.push(
      <Icon
        as={MdOutlineStarOutline}
        color={starColor}
        key={index++}
        boxSize={starSize}
      />
    );
  }
  return <HStack>{stars}</HStack>;
};

export default StarRating;
