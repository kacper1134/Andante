import {
  Checkbox,
  Collapse,
  HStack,
  VStack,
  Text,
  useBreakpointValue,
} from "@chakra-ui/react";
import { Category } from "./ProductCategory";

type SubcategoriesFilterProps = {
  category: Category;
  setCheckedItems: React.Dispatch<React.SetStateAction<boolean[]>>;
  checkedItems: boolean[];
  isOpen: boolean;
};

const SubcategoriesFilter = ({
  category,
  isOpen,
  checkedItems,
  setCheckedItems,
}: SubcategoriesFilterProps) => {
  const fontSize = useBreakpointValue({
    base: "10px",
    sm: "12px",
    md: "14px",
    lg: "16px",
    xl: "18px",
  });

  return (
    <Collapse in={isOpen} animateOpacity>
      <VStack alignItems="flex-start">
        {category.subcategories.map((subcategory, index) => (
          <HStack key={index}>
            <Checkbox disabled opacity={0} _disabled={{ cursor: "auto" }} />
            <Checkbox
              isChecked={checkedItems[index]}
              colorScheme="primary"
              onChange={(e) => {
                const checks = checkedItems.slice();
                checks[index] = e.target.checked;
                setCheckedItems(checks.slice());
              }}
            />
            <Text fontSize={fontSize} textStyle="p">{subcategory.label}</Text>
          </HStack>
        ))}
      </VStack>
    </Collapse>
  );
};

export default SubcategoriesFilter;
