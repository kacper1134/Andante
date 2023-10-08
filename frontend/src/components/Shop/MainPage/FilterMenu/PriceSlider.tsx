import {
  Box,
  Flex,
  Text,
  useToken,
  RangeSlider,
  RangeSliderThumb,
  RangeSliderFilledTrack,
  RangeSliderTrack,
} from "@chakra-ui/react";
import { useState } from "react";
import { MdGraphicEq } from "react-icons/md";

const fontSize = {
  base: "13px",
  sm: "15px",
  md: "16px",
  lg: "19px",
  xl: "21px",
};

type PriceSliderProps = {
  priceRange: number[];
  setPriceRange: React.Dispatch<React.SetStateAction<number[]>>;
};

const PriceSlider = ({
  priceRange,
  setPriceRange,
}: PriceSliderProps) => {
  const [primary100, primary500] = useToken("colors", [
    "primary.100",
    "primary.500",
  ]);
  const [displayedPrice, setDisplayedPrice] = useState(priceRange);
  const topPrice = 2000;

  const handlePriceRangeChange = (newPriceRange: number[]) => {
    const low = newPriceRange[0];
    const hight = newPriceRange[1];

    if (low !== priceRange[0] || hight !== priceRange[1]) {
      setPriceRange(newPriceRange);
    }
  };

  return (
    <Flex direction="column" p="5%" fontSize={fontSize}>
      <Text fontWeight="bold" textStyle="h1">Price</Text>
      <RangeSlider
        defaultValue={priceRange}
        min={0}
        max={topPrice}
        width="90%"
        mt="1%"
        ml="5%"
        step={10}
        onChange={(val) => setDisplayedPrice(val)}
        onChangeEnd={(val) => handlePriceRangeChange(val)}
      >
        <RangeSliderTrack bg={primary100}>
          <RangeSliderFilledTrack bg={primary500} />
        </RangeSliderTrack>
        <RangeSliderThumb boxSize={6} index={0}>
          <Box color={primary500} as={MdGraphicEq} />
        </RangeSliderThumb>
        <RangeSliderThumb boxSize={6} index={1}>
          <Box color={primary500} as={MdGraphicEq} />
        </RangeSliderThumb>
      </RangeSlider>
      <Text fontWeight="bold" pt="3%" textStyle="p">
        ${displayedPrice[0]} - ${displayedPrice[1]}
      </Text>
    </Flex>
  );
};

export default PriceSlider;
