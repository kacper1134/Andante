import { Box } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { IconType } from "react-icons";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../../../store";
import { innerActions } from "../../../../store/inner/innerSlice";
import ProductCategoryButton from "./ProductCategoryButton";
import SubcategoriesFilter from "./SubcategoriesFilter";

export enum ProductType {
  AMPLIFIERS = "Amplifiers",
  GRAMOPHONES = "Gramophones",
  HEADPHONES = "Headphones",
  MICROPHONES = "Microphones",
  SPEAKERS = "Speakers",
  SUBWOOFERS = "Subwoofers",
};

export interface Subcategory {
  label: string,
  value: string,
};

export type Category = {
  type: ProductType,
  subcategories: Subcategory[],
  icon: IconType,
  fieldName: string,
};

type ProductCategoryProps = {
  category: Category;
};

const ProductCategory = ({ category }: ProductCategoryProps) => {
  const selectedCategory = useSelector((state: RootState) => state.inner.filterState.selectedCategory);
  const [checkedItems, setCheckedItems] = useState(
   selectedCategory && selectedCategory.category === category.type ? category.subcategories.map((value) => selectedCategory.selectedSubcategories.includes(value)) : category.subcategories.map((_) => false)
  );
  const dispatch = useDispatch();
  const allChecked = checkedItems.every(Boolean);
  const isIndeterminate = checkedItems.some(Boolean) && !allChecked;
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    if((selectedCategory && selectedCategory.category === category.type) || checkedItems.some(Boolean)) {
      const selectedSubcategories = category.subcategories.filter((value, index) => checkedItems[index]);

      dispatch(innerActions.setCategoryState({
        category: category.type,
        subcategoryName: category.fieldName,
        selectedSubcategories: selectedSubcategories,
        selectedAll: checkedItems.every(Boolean),
      }))
    }
  }, [category, dispatch, checkedItems]);

  useEffect(() => {
    if (selectedCategory && selectedCategory.category !== category.type) {
      setCheckedItems(category.subcategories.map((_) => false));
    }
  }, [selectedCategory])

  return (
    <Box mb="7%">
      <ProductCategoryButton
        category={category}
        allChecked={allChecked}
        isIndeterminate={isIndeterminate}
        setCheckedItems={setCheckedItems}
        isOpen={isOpen}
        toggleOpen={setIsOpen}
      />
      <SubcategoriesFilter
        category={category}
        isOpen={isOpen}
        setCheckedItems={setCheckedItems}
        checkedItems={checkedItems}
      />
    </Box>
  );
};

export default ProductCategory;
