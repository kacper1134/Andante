import { ChevronLeftIcon, ChevronRightIcon } from "@chakra-ui/icons";
import {
  Button,
  HStack,
  IconButton,
  NumberInput,
  NumberInputField,
  useBreakpointValue,
  useToken,
} from "@chakra-ui/react";

export interface PageSelectorProps {
  currentPage: number;
  setCurrentPage: React.Dispatch<React.SetStateAction<number>>;
  totalNumberOfPages: number;
}

const PageSelector: React.FC<PageSelectorProps> = ({
  currentPage,
  setCurrentPage,
  totalNumberOfPages,
}) => {
  const color = useToken("colors", "primary.400");
  const backgroundColor = "white";
  const hoverColor = "primary.50";
  const activeColor = "primary.100";

  const size = useBreakpointValue({
    base: 24,
    md: 26,
    lg: 28,
    xl: 30,
    "2xl": 32,
  })!;

  const changeCurrentPageHandler = (newPage: number) => {
    if (newPage >= 0 && newPage < totalNumberOfPages) {
      setCurrentPage(newPage);
    }
  };

  return (
    <HStack alignSelf="center" py="16px">
      <NavigationButton
        icon={
          <ChevronLeftIcon
            boxSize={size + "px"}
            color={color}
          />
        }
        onClick={() => changeCurrentPageHandler(currentPage - 1)}
        size={size}
        label="Previous page"
        color={color}
        backgroundColor={backgroundColor}
        isDisabled={currentPage < 1}
        hoverColor={hoverColor}
        activeColor={activeColor}
      />
      {currentPage > 0 && (
        <PageButton
          page={currentPage - 1}
          color={color}
          size={size}
          backgroundColor={backgroundColor}
          hoverColor={hoverColor}
          activeColor={activeColor}
          isSelected={false}
          onClick={() => changeCurrentPageHandler(currentPage - 1)}
        />
      )}
      <PageButton
        page={currentPage}
        color={color}
        size={size}
        backgroundColor={backgroundColor}
        hoverColor={hoverColor}
        activeColor={activeColor}
        isSelected={true}
        onClick={() => changeCurrentPageHandler(currentPage)}
      />
      {currentPage < totalNumberOfPages - 2 && (
        <SelectionButton
          color={color}
          size={size}
          totalNumberOfPages={totalNumberOfPages}
          backgroundColor={backgroundColor}
          changeCurrentPageHandler={changeCurrentPageHandler}
        />
      )}
      {currentPage < totalNumberOfPages - 1 && (
        <PageButton
          page={totalNumberOfPages - 1}
          color={color}
          size={size}
          backgroundColor={backgroundColor}
          hoverColor={hoverColor}
          activeColor={activeColor}
          isSelected={false}
          onClick={() => changeCurrentPageHandler(totalNumberOfPages - 1)}
        />
      )}
      <NavigationButton
        icon={
          <ChevronRightIcon
            boxSize={size + "px"}
            color={color} 
          />
        }
        onClick={() => changeCurrentPageHandler(currentPage + 1)}
        size={size}
        label="Previous page"
        color={color}
        backgroundColor={backgroundColor}
        isDisabled={currentPage === totalNumberOfPages - 1}
        hoverColor={hoverColor}
        activeColor={activeColor}
      />
    </HStack>
  );
};

interface NavigationButtonProps {
  icon: React.ReactElement;
  size: number;
  label: string;
  color: string;
  backgroundColor: string;
  isDisabled: boolean;
  hoverColor: string;
  activeColor: string;
  onClick: () => void;
}

const NavigationButton: React.FC<NavigationButtonProps> = ({
  icon,
  label,
  color,
  backgroundColor,
  size,
  isDisabled,
  hoverColor,
  activeColor,
  onClick,
}) => {
  return (
    <IconButton
      icon={icon}
      border={`1px solid ${color}`}
      w={size + 12 + "px"}
      h={size + 12 + "px"}
      aria-label={label}
      borderRadius="50%"
      backgroundColor={backgroundColor}
      _hover={{ backgroundColor: hoverColor }}
      _active={{ backgroundColor: activeColor }}
      disabled={isDisabled}
      onClick={onClick}
    />
  );
};

interface PageButtonProps {
  page: number;
  color: string;
  size: number;
  backgroundColor: string;
  hoverColor: string;
  activeColor: string;
  isSelected: boolean;
  onClick: () => void;
}

const PageButton: React.FC<PageButtonProps> = ({
  page,
  color,
  backgroundColor,
  hoverColor,
  activeColor,
  size,
  isSelected,
  onClick,
}) => {
  return (
    <Button
      color={color}
      border={`1px solid ${color}`}
      w={size + 12 + "px"}
      h={size + 12 + "px"}
      size={size + "px"}
      borderRadius="50%"
      cursor="pointer"
      backgroundColor={isSelected ? activeColor : backgroundColor}
      userSelect="none"
      onClick={onClick}
      _hover={{ backgroundColor: hoverColor }}
      _active={{ backgroundColor: activeColor }}
    >
      {page + 1}
    </Button>
  );
};

interface SelectionButtonProps {
  color: string;
  size: number;
  backgroundColor: string;
  totalNumberOfPages: number;
  changeCurrentPageHandler: (newPage: number) => void;
}

const SelectionButton: React.FC<SelectionButtonProps> = ({
  color,
  size,
  backgroundColor,
  totalNumberOfPages,
  changeCurrentPageHandler,
}) => {
  return (
    <NumberInput min={1} max={totalNumberOfPages} size={size + "px"}>
      <NumberInputField
        _focusVisible={{ outline: "none" }}
        w={size + 12 + "px"}
        h={size + 12 + "px"}
        borderRadius="50%"
        color={color}
        textAlign="center"
        placeholder="..."
        borderColor={color}
        _hover={{ borderColor: color }}
        backgroundColor={backgroundColor}
        onChange={(event) => changeCurrentPageHandler(+event.target.value - 1)}
      />
    </NumberInput>
  );
};

export default PageSelector;
