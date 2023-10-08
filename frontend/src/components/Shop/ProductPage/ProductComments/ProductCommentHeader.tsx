import {
  HStack,
  Icon,
  Spacer,
  Text,
  Avatar,
  Box,
  VStack,
} from "@chakra-ui/react";
import { DateTime } from "luxon";
import { useState } from "react";
import { AiFillEdit } from "react-icons/ai";
import { FaUserCircle } from "react-icons/fa";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { RootState } from "../../../../store";
import { CommentOutputDTO } from "../../../../store/api/result/dto/product/CommentOutputDTO";

type ProductCommentHeaderProps = {
  comment: CommentOutputDTO;
  avatarUrl?: string;
  setIsEdit: React.Dispatch<React.SetStateAction<boolean>>;
};

const ProductCommentHeader = ({
  comment,
  avatarUrl,
  setIsEdit,
}: ProductCommentHeaderProps) => {
  const navigate = useNavigate();
  const avatarSize = {
    base: "24px",
    sm: "28px",
    md: "32px",
    lg: "36px",
    xl: "40px",
  };

  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  const isCurrentUserAuthor =
    userDetails?.personal.username === comment.username;

  return (
    <HStack width="95%">
      {avatarUrl ? (
        <Avatar
          src={avatarUrl}
          boxSize={avatarSize}
          onClick={() => navigate(`/profile/community/${comment.username}`)}
          cursor="pointer"
        />
      ) : (
        <Icon
          boxSize={avatarSize}
          as={FaUserCircle}
          onClick={() => navigate(`/profile/community/${comment.username}`)}
          cursor="pointer"
        />
      )}
      <Text
        pl="1%"
        fontSize={{
          base: "12px",
          sm: "14px",
          md: "16px",
          lg: "18px",
          xl: "20px",
        }}
        textStyle="p"
      >
        {comment.username}
      </Text>
      <Spacer />
      <Box>
        <Text
          color="gray"
          fontSize={{
            base: "9px",
            sm: "10px",
            md: "11px",
            lg: "12px",
            xl: "13px",
          }}
          textStyle="p"
        >
          {DateTime.fromISO(comment.creationTimestamp).toLocaleString(
            DateTime.DATETIME_MED
          )}
        </Text>
        {isCurrentUserAuthor && (
          <>
            <HStack
              fontSize={{
                base: "9px",
                sm: "10px",
                md: "11px",
                lg: "12px",
                xl: "13px",
              }}
            >
              <Spacer />
              <HStack
                cursor="pointer"
                onClick={() => setIsEdit((prev) => !prev)}
              >
                <Icon as={AiFillEdit} color="orange" />
                <Text color="primary.600" textStyle="h1">
                  Edit
                </Text>
              </HStack>
            </HStack>
          </>
        )}
      </Box>
    </HStack>
  );
};

export default ProductCommentHeader;
