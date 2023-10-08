import { VStack, Text, HStack, Spacer } from "@chakra-ui/react";
import { Dispatch, SetStateAction } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../../store";

type PickupLocationEditProps = {
  setIsModalOpen: Dispatch<SetStateAction<boolean>>;
};

const PickupLocationEdit = ({ setIsModalOpen }: PickupLocationEditProps) => {
  const location = useSelector((state:RootState) => state.cart.deliveryPickupLocation)!;
  return (
    <VStack alignItems="flex-start" alignSelf="flex-start" w="100%" boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)" p="10px" >
      <HStack w="100%">
        <Text fontSize="15px" textStyle="h1">{location.brand}</Text> 
        <Spacer />
        <Text fontSize="12px" color="primary.700" onClick={() => setIsModalOpen(true)} textStyle="h1">Change</Text>
      </HStack>
      <Text fontSize="12px" textStyle="p">
        {location.street} {location.streetNumber}
      </Text>
      <Text fontSize="12px" textStyle="p">
        {location.postCode} {location.city}
      </Text>
    </VStack>
  );
};

export default PickupLocationEdit;
