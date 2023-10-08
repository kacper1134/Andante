import { Flex, Text } from "@chakra-ui/react";
import ProductCategory, { Category, ProductType } from "./ProductCategory";
import { BiHeadphone, BiSpeaker, BiMicrophone } from "react-icons/bi";
import { GiTheater } from "react-icons/gi";
import { MdSurroundSound } from "react-icons/md";
import { AmplifierType } from "../../../../store/api/result/dto/product/base/AmplifiersOutputDTO";
import { FaRecordVinyl } from "react-icons/fa";
import { ConnectivityTechnology } from "../../../../store/api/result/dto/product/base/GramophonesOutputDTO";
import { ConstructionType } from "../../../../store/api/result/dto/product/base/HeadphonesOutputDTO";
import { MicrophoneType } from "../../../../store/api/result/dto/product/base/MicrophonesOutputDTO";
import { SubwooferType } from "../../../../store/api/result/dto/product/base/SubwoofersOutputDTO";

const headingSize = {
  base: "22px",
  sm: "24px",
  md: "26px",
  lg: "28px",
  xl: "30px",
};

const productHierarchy: Category[] = [
  {
    type: ProductType.AMPLIFIERS,
    icon: MdSurroundSound,
    subcategories: Object.entries(AmplifierType).map(type => {return {label: type[1], value: type[0]}}),
    fieldName: "type",
  },
  {
    type: ProductType.GRAMOPHONES,
    icon: FaRecordVinyl,
    subcategories:  Object.entries(ConnectivityTechnology).map(type => {return {label: type[1], value: type[0]}}),
    fieldName: "connectivityTechnology",
  },
  {
    type: ProductType.HEADPHONES,
    icon: BiHeadphone,
    subcategories:  Object.entries(ConstructionType).map(type => {return {label: type[1], value: type[0]}}),
    fieldName: "constructionType",
  },
  {
    type: ProductType.MICROPHONES,
    icon: BiMicrophone,
    subcategories:  Object.entries(MicrophoneType).map(type => {return {label: type[1], value: type[0]}}),
    fieldName: "type",
  },
  {
    type: ProductType.SPEAKERS,
    icon: BiSpeaker,
    subcategories: [
      {label: "Wired", value: "false"},
      {label: "Wireless", value: "true"},
    ],
    fieldName: "wireless",
  },
  {
    type: ProductType.SUBWOOFERS,
    icon: GiTheater,
    subcategories:  Object.entries(SubwooferType).map(type => {return {label: type[1], value: type[0]}}),
    fieldName: "type",
  },
]

const CategoriesFilter = () => {
  return (
    <Flex direction="column" p="5%">
      <Text fontSize={headingSize} pb="3" textStyle="h1">
        Categories
      </Text>
      {productHierarchy.map((category, index) => (
        <ProductCategory key={index} category={category} />
      ))}
    </Flex>
  );
};

export default CategoriesFilter;
