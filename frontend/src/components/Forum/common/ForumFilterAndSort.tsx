import { SearchIcon } from "@chakra-ui/icons";
import {
  Box,
  HStack,
  Input,
  InputGroup,
  InputLeftElement,
  Select,
  Spacer,
  useToken,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { TopicSortingOrder } from "../../../store/api/forum-api-slice";
import { buttonTextFontSize, topicContentMarginY } from "./ForumDimensions";

export type OptionType = {
  value: string;
  text: string;
};

type ForumFilterAndSortProps = {
  options: OptionType[];
  isMarginLeft: boolean;
  placeholder: string;
  setSortingOrder?: React.Dispatch<React.SetStateAction<TopicSortingOrder>>;
  setSearchPhrase?: React.Dispatch<React.SetStateAction<string>>;
  setReload?: React.Dispatch<React.SetStateAction<boolean>>;
  disableSortFilter?: boolean;
};

const ForumFilterAndSort = ({
  options,
  placeholder,
  isMarginLeft,
  setSortingOrder,
  setSearchPhrase,
  setReload,
  disableSortFilter,
}: ForumFilterAndSortProps) => {
  const optionStyle = { fontWeight: "bold" };
  const color = useToken("colors", "primary.400");
  const [phrase, setPhrase] = useState("");
  const location = useLocation();

  useEffect(() => {
    setPhrase("");
  }, [location]);

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      setSearchPhrase!(phrase);
      if (setReload) {
        setReload(true);
      }
    }, 1000);
    return () => clearTimeout(delayDebounceFn);
  }, [phrase, setReload, setSearchPhrase]);

  return (
    <HStack width="100%" pb={topicContentMarginY}>
      <InputGroup
        w={{ base: "50%", sm: "45%", md: "40%", lg: "35%", xl: "30%" }}
        ml={isMarginLeft ? topicContentMarginY : 0}
      >
        <InputLeftElement
          pointerEvents="none"
          children={<SearchIcon color="primary.200" />}
        />
        <Input
          fontSize={buttonTextFontSize}
          value={phrase}
          color={color}
          variant="outline"
          textStyle="p"
          placeholder={placeholder}
          onChange={(event) => setPhrase(event.target.value)}
          _placeholder={{ color: "primary.200" }}
          disabled={disableSortFilter}
        />
      </InputGroup>
      <Spacer />
      <Box px={topicContentMarginY}>
        <Select
          variant="filled"
          width="fit-content"
          border="1px solid gray!"
          color={color}
          bg="white"
          fontWeight="bold"
          cursor="pointer"
          textStyle="p"
          fontSize={buttonTextFontSize}
          disabled={disableSortFilter}
          onChange={(event) => {
            if (setReload) {
              setReload(true);
            }
            setSortingOrder!(
              TopicSortingOrder[event.target.value as TopicSortingOrder]
            );
          }}
        >
          {options.map((option, index) => (
            <option key={index} value={option.value} style={optionStyle}>
              {option.text}
            </option>
          ))}
        </Select>
      </Box>
    </HStack>
  );
};

export default ForumFilterAndSort;
