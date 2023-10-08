import { ChevronLeftIcon, ChevronRightIcon } from "@chakra-ui/icons";
import { Input, Text, HStack, Spacer } from "@chakra-ui/react";

type PageChangerProps = {
  page: number;
  setPage: React.Dispatch<React.SetStateAction<number>>;
  numberOfPages: number;
  setReload?: React.Dispatch<React.SetStateAction<boolean>>;
};

const PageChanger = ({
  page,
  setPage,
  numberOfPages,
  setReload,
}: PageChangerProps) => {
  const margin = {
    base: "12px",
    sm: "14px",
    md: "16px",
    lg: "18px",
    xl: "20px",
  };

  const controlsSize = {
    base: "24px",
    sm: "28px",
    md: "32px",
    lg: "36px",
    xl: "40px",
  };

  const fontSize = {
    base: "12px",
    sm: "14px",
    md: "16px",
    lg: "18px",
    xl: "20px",
  };

  const onPageNumberChange = (newPage: number) => {
    if (newPage < 0) setPage(0);
    else if (newPage >= numberOfPages) {
      setPage(numberOfPages - 1);
    } else {
      if (setReload) setReload(true);
      setPage(newPage);
    }
  };

  return (
    <HStack pb={margin} alignSelf="flex-end" pe="16px">
      <Spacer />
      {page > 0 && (
        <ChevronLeftIcon
          fontSize={controlsSize}
          cursor="pointer"
          onClick={() => onPageNumberChange(page - 1)}
        />
      )}
      <Input
        type="number"
        value={page + 1}
        textAlign="center"
        fontSize={fontSize}
        bg="white"
        height="fit-content"
        width={{ base: "60px", sm: "50px", md: "60px", lg: "70px", xl: "80px" }}
        onChange={(value) => onPageNumberChange(+value.currentTarget.value - 1)}
        textStyle="p"
      />
      <Text fontSize={fontSize} textStyle="p">
        of {numberOfPages}
      </Text>
      {page < numberOfPages - 1 && (
        <ChevronRightIcon
          fontSize={controlsSize}
          cursor="pointer"
          onClick={() => onPageNumberChange(page + 1)}
        />
      )}
    </HStack>
  );
};

export default PageChanger;
