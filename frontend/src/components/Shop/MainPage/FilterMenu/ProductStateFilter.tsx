import { Checkbox, Flex, HStack, Icon, Text } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { IconType } from "react-icons";
import { FaMoneyBillWave } from "react-icons/fa";
import { GiChoice } from "react-icons/gi";
import { IoMdCheckmarkCircleOutline } from "react-icons/io";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../../../store";
import { innerActions } from "../../../../store/inner/innerSlice";

export enum State {
  ON_SALE = "On sale",
  ANDANTE_CHOICE = "Andante choice",
  AVAILABLE = "Available",
};

type ProductState = {
  name: State;
  icon: IconType;
};

const states: ProductState[] = [
  { name: State.ON_SALE, icon: FaMoneyBillWave },
  { name: State.ANDANTE_CHOICE, icon: GiChoice },
  { name: State.AVAILABLE, icon: IoMdCheckmarkCircleOutline },
];

const headerSize = {
  base: "13px",
  sm: "15px",
  md: "16px",
  lg: "19px",
  xl: "21px",
};

const fontSize = {
  base: "10px",
  sm: "12px",
  md: "14px",
  lg: "16px",
  xl: "18px",
};

const ProductStateFilter = () => {
  const persistedState = useSelector((state: RootState) => state.inner.filterState.selectedStates);
  const [checkedItems, setCheckedItems] = useState(states.map((value) => persistedState.includes(value.name)));
  const dispatch = useDispatch();

  useEffect(() => {
    const selectedStates = checkedItems.map((_, index) => states[index].name)
      .filter((_, index) => checkedItems[index]);

    dispatch(innerActions.setSelectedStates(selectedStates));
  }, [checkedItems, dispatch]);

  return (
    <Flex direction="column" p="5%">
      <Text fontWeight="bold" fontSize={headerSize} textStyle="h1">
        State
      </Text>
      {states.map((state, index) => (
        <Flex key={index} pt="5%">
          <Checkbox
            isChecked={persistedState.includes(state.name)}
            colorScheme="primary"
            onChange={(e) => {
              const checks = checkedItems.slice();
              checks[index] = e.target.checked;
              setCheckedItems(checks.slice());
            }}
          />
          <HStack width="100%" pl="5%" >
            <Icon as={state.icon} />
            <Text fontSize={fontSize} textStyle="p">
              {state.name}
            </Text>
          </HStack>
        </Flex>
      ))}
    </Flex>
  );
};

export default ProductStateFilter;
