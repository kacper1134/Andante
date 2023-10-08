import { Flex, useBreakpointValue, Spacer, Input, InputGroup, InputLeftElement } from "@chakra-ui/react";
import { Text, Menu, MenuButton, MenuItem, MenuList, Button } from '@chakra-ui/react'
import { ChevronDownIcon } from "@chakra-ui/icons";
import { SearchIcon } from "@chakra-ui/icons";
import { ProductSortingOrder } from "../../../store/api/productSlice";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { innerActions } from "../../../store/inner/innerSlice";

function toDisplayValue(sortingOption: ProductSortingOrder): string {
    return sortingOption.split("_")
        .map(word => word.substring(0 , 1) + word.substring(1).toLowerCase())
        .toString()
        .replace(",", " ");
}

export interface ResultsBarProps {
    setSortingOrder: Dispatch<SetStateAction<ProductSortingOrder>>,
    resultRange: string,
    totalCount: number,
}

const ResultsBar: React.FC<ResultsBarProps> = ({setSortingOrder, resultRange, totalCount}) => {
    const [query, setQuery] = useState("");
    const dispatch = useDispatch();

    useEffect(() => {
        const queryTimeout = setTimeout(() => dispatch(innerActions.setQuery(query)), 1000);

        return () => clearTimeout(queryTimeout);
    }, [query, dispatch]);

    const fontSize = useBreakpointValue({
        base: 12,
        md: 14,
        lg: 16,
        xl: 18,
    })

    const px = useBreakpointValue({
        base: 8,
        md: 10,
        lg: 12,
        xl: 14,
        '2xl': 16,
    })!;
  
    return (
        <Flex bg='purple.50' px={`${px}px`} py={`${px / 2}px`}>
            <Text alignSelf="center" textStyle="h2" color="primary.400" fontSize={`${fontSize}px`}>Results {resultRange} from {totalCount}</Text>
            <Spacer />
            <InputGroup w="300px" px="8px" borderColor="primary.300">
                <InputLeftElement pointerEvents="none" children={<SearchIcon color="primary.100" />}/>
                <Input placeholder="What do you want to buy?" color="primary.300" focusBorderColor="primary.300" value={query} onChange={(e) => setQuery(e.target.value)} />
            </InputGroup>
            <Menu>
                <MenuButton as={Button} rightIcon={<ChevronDownIcon />} 
                backgroundColor="purple.100"  
                color="primary.800" 
                fontSize={`${fontSize}px`}
                _hover={{backgroundColor: "purple.200"}} 
                _active={{backgroundColor: "purple.200"}}>
                    Sort By
                </MenuButton>
                <MenuList color="primary.800" bgColor="purple.100">
                    {Object.values(ProductSortingOrder).map((value, index) => <MenuItem key={index} onClick={() => setSortingOrder(value)} _hover={{backgroundColor: "purple.200"}} _active={{backgroundColor: "purple.200"}}>{toDisplayValue(value)}</MenuItem>)}
                </MenuList>
            </Menu>
        </Flex>
    );
  };
  
  export default ResultsBar;