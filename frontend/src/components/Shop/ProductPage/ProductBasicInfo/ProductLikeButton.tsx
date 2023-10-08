import { HStack, Icon, Tooltip } from "@chakra-ui/react";
import { motion } from "framer-motion";
import { BsSuitHeart, BsSuitHeartFill } from "react-icons/bs";
import { useChangeObservationStatusMutation } from "../../../../store/api/productSlice";

const tooltipSize = {
  base: "11px",
  sm: "12px",
  md: "13px",
  lg: "14px",
  xl: "15px",
  "2xl": "16px",
};

type ProductLikeButtonProps = {
  productId: number,
  isLiked: boolean,
  userEmail?: string,
  setIsLiked: React.Dispatch<React.SetStateAction<boolean>>,
  fontSize: {
    base: string,
    sm: string,
    md: string,
    lg: string,
    xl: string,
    "2xl": string,
  },
};

const ProductLikeButton = ({
  productId,
  userEmail,
  isLiked,
  setIsLiked,
  fontSize,
}: ProductLikeButtonProps) => {
  const [ changeObservationStatus] = useChangeObservationStatusMutation();


  const changeStatus = () => {
    if (userEmail) {
      changeObservationStatus({productId: productId, email: userEmail})
      .then(() => setIsLiked(!isLiked)); 
    }
  }
  
  return (
    <Tooltip
      hasArrow
      fontSize={tooltipSize}
      label={isLiked ? "Remove from favourite" : "Add to favourite"}
      textStyle="p"
      bg="secondary.400"
      color="white"
    >
      <HStack height="fit-content" width="fit-content" position="relative">
        <Icon
          fontSize={fontSize}
          color="secondary.400"
          as={BsSuitHeart}
          cursor="pointer"
          position="absolute"
          right="0"
          onClick={changeStatus}
        />
        <HStack
          key={isLiked ? 1 : 0}
          as={motion.div}
          whileHover={{ opacity: 1 }}
          initial={{ opacity: isLiked ? 1 : 0 }}
        >
          <Icon
            fontSize={fontSize}
            color="secondary.400"
            as={BsSuitHeartFill}
            onClick={changeStatus}
            cursor="pointer"
          />
        </HStack>
      </HStack>
    </Tooltip>
  );
};

export default ProductLikeButton;
