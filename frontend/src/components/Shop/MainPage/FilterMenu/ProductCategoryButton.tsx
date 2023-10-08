import { ChevronDownIcon, ChevronUpIcon } from "@chakra-ui/icons";
import { Checkbox, HStack, Icon, Text } from "@chakra-ui/react";
import { Category } from "./ProductCategory";

type ProductCategoryButtonProps = {
  category: Category;
  allChecked: boolean;
  isIndeterminate: boolean;
  setCheckedItems: React.Dispatch<React.SetStateAction<boolean[]>>;
  isOpen: boolean;
  toggleOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const fontSize = {
  base: "14px",
  sm: "16px",
  md: "18px",
  lg: "20px",
  xl: "22px",
};

const ProductCategoryButton = ({
  category,
  allChecked,
  isIndeterminate,
  isOpen,
  toggleOpen,
  setCheckedItems,
}: ProductCategoryButtonProps) => {
  return (
    <HStack fontSize={fontSize}>
      <Checkbox
        isChecked={allChecked}
        colorScheme="primary"
        isIndeterminate={isIndeterminate}
        onChange={(e) =>
          setCheckedItems(category.subcategories.map((_) => e.target.checked))
        }
      ></Checkbox>
      <HStack>
        <Icon as={category.icon} />
        <Text cursor="default" textStyle="p">
          {category.type}
          {!isOpen && (
            <ChevronDownIcon
              cursor="pointer"
              onClick={() => toggleOpen(true)}
            />
          )}
          {isOpen && (
            <ChevronUpIcon cursor="pointer" onClick={() => toggleOpen(false)} />
          )}
        </Text>
      </HStack>
    </HStack>
  );
};

export default ProductCategoryButton;
