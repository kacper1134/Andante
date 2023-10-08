import { HStack, Spacer, Text, Avatar } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

type PostCommentHeaderProps = {
  author: string;
  date: string;
  userImage: string;
};

const PostCommentHeader = ({
  author,
  date,
  userImage,
}: PostCommentHeaderProps) => {
  const timestamp = new Date(date);
  const navigate = useNavigate();

  return (
    <HStack width="95%" textStyle="p">
      <Avatar
        boxSize={{
          base: "24px",
          sm: "28px",
          md: "32px",
          lg: "36px",
          xl: "40px",
        }}
        src={userImage}
        onClick={() => navigate(`/profile/community/${author}`)}
        cursor="pointer"
      />
      <Text
        pl="1%"
        fontSize={{
          base: "12px",
          sm: "14px",
          md: "16px",
          lg: "18px",
          xl: "20px",
        }}
      >
        {author}
      </Text>
      <Spacer />
      <Text
        color="gray"
        fontSize={{
          base: "9px",
          sm: "10px",
          md: "11px",
          lg: "12px",
          xl: "13px",
        }}
      >
        {timestamp.getDate()}{" "}
        {timestamp.toLocaleString("default", { month: "long" })}{" "}
        {timestamp.getFullYear()} {timestamp.toLocaleTimeString()}
      </Text>
    </HStack>
  );
};

export default PostCommentHeader;
