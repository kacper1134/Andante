import { Box, useBreakpointValue } from "@chakra-ui/react";
import parse from "html-react-parser";

export interface PostContentProps {
  content?: string;
}

const PostContent: React.FC<PostContentProps> = ({ content }) => {
  const zoomValue = useBreakpointValue({
    base: 0.5,
    sm: 0.6,
    md: 0.7,
    lg: 0.9,
    xl: 1,
  });

  return (
    <Box position="relative" w="80%" style={{ zoom: zoomValue }}>
      <Box textStyle="p">{parse(content ?? "")}</Box>
    </Box>
  );
};

export default PostContent;
