import {
  useBreakpointValue,
  Flex,
  Spacer,
  Button,
  Text,
  Icon,
  HStack,
  useToken
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import AverageCustomerReview from "./AverageCustomerReview";
import CategoriesFilter from "./CategoriesFilter";
import PriceSlider from "./PriceSlider";
import ProductStateFilter from "./ProductStateFilter";
import { AiOutlineSearch } from "react-icons/ai";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { innerActions } from "../../../../store/inner/innerSlice";
import { RootState } from "../../../../store";

export interface FilterMenuProps {
  width: string,
  hasBoxShadow: boolean,
  shouldFillAvailableSpace: boolean,
  menuHeight?: string,
};

const FilterMenu = ({ width, hasBoxShadow, shouldFillAvailableSpace, menuHeight }: FilterMenuProps) => {
  const margin = useBreakpointValue({
    base: "6px",
    sm: "7px",
    md: "8px",
    lg: "9px",
    xl: "10px",
  });
  
  const borderColor = useToken("colors", "primary.200"); 
  const persistedRating = useSelector((state: RootState) => state.inner.filterState.minimumRating);
  const [rating, setRating] = useState(persistedRating);
  const persistedPriceRange = useSelector((state: RootState) => state.inner.filterState.priceRange);
  const [priceRange, setPriceRange] = useState([persistedPriceRange.min ?? 0, persistedPriceRange.max ?? 2000]);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const boxShadow = hasBoxShadow ? "0px 0px 0.8px rgba(0, 0, 0, 0.11)," +
  "0px 0px 1.7px rgba(0, 0, 0, 0.087)," +
  "0px 0px 2.9px rgba(0, 0, 0, 0.076)," +
  "0px 0px 4.3px rgba(0, 0, 0, 0.068)," +
  "0px 0px 6.3px rgba(0, 0, 0, 0.061)," +
  "0px 0px 8.9px rgba(0, 0, 0, 0.055)," +
  "0px 0px 12.6px rgba(0, 0, 0, 0.049)," +
  "0px 0px 18.3px rgba(0, 0, 0, 0.042)," +
  "0px 0px 28.1px rgba(0, 0, 0, 0.034)," +
  "0px 0px 50px rgba(0, 0, 0, 0.023)" : "none";
  
  const border = hasBoxShadow ? "none" : `1px solid ${borderColor}`;

  useEffect(() => {
    dispatch(innerActions.setPriceRange({min: priceRange[0], max: priceRange[1]}));
  }, [priceRange, dispatch]);

  return (
    <Flex
      direction="column"
      minH={shouldFillAvailableSpace ? '100vh' : ''}
      h={shouldFillAvailableSpace ? '' : 'fit-content'}
      position={shouldFillAvailableSpace ? 'static' : 'sticky'}
      top={menuHeight ?? '0'}
      minWidth={width}
      my={margin}
      ml={margin}
      boxShadow={boxShadow}
      borderRight={border}
      rounded={hasBoxShadow ? "lg" : ""}
    >
      <CategoriesFilter />
      <AverageCustomerReview rating={rating} setRating={setRating} />
      <PriceSlider
        priceRange={priceRange}
        setPriceRange={setPriceRange}
      />
      {shouldFillAvailableSpace && <Spacer />}
      {hasBoxShadow && <Button
        mt="12px"
        as={motion.button}
        whileHover={{ scale: 1.1 }}
        width="fit-content"
        height="fit-content"
        alignSelf="flex-end"
        mb="5%"
        mr="5%"
        py="2%"
        colorScheme="primary"
        rounded="2xl"
        onClick={() => navigate("/shop/filtered")}
      >
        <HStack
          fontSize={{
            base: "10px",
            sm: "12px",
            md: "14px",
            lg: "16px",
            xl: "18px",
          }}
        >
          <Icon as={AiOutlineSearch}></Icon>
          <Text textStyle="p">Search</Text>
        </HStack>
      </Button>}
    </Flex>
  );
};

export default FilterMenu;
