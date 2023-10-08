import { SearchIcon } from "@chakra-ui/icons";
import { Box, Input, InputGroup, InputLeftElement } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { postAuthorFontSize } from "./BlogMainPageSizes";

type BlogFilterProps = {
  setSearch: React.Dispatch<React.SetStateAction<string>>;
};

const BlogFilter = ({ setSearch }: BlogFilterProps) => {
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const delaySearch = setTimeout(() => {
      setSearch(searchTerm);
    }, 1000);
    return () => clearTimeout(delaySearch);
  }, [searchTerm, setSearch]);

  return (
    <Box w="90%">
      <InputGroup
        w={{ base: "50%", sm: "45%", md: "40%", lg: "35%", xl: "30%" }}
      >
        <InputLeftElement
          fontSize={postAuthorFontSize}
          pointerEvents="none"
          children={<SearchIcon color="primary.200" />}
        />
        <Input
          fontSize={postAuthorFontSize}
          variant="outline"
          color="primary.400"
          placeholder="Find blog posts"
          _placeholder={{ color: "primary.200" }}
          textStyle="p"
          value={searchTerm}
          onChange={(event) => setSearchTerm(event.target.value)}
        />
      </InputGroup>
    </Box>
  );
};

export default BlogFilter;
