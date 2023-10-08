import {
  Button,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Spinner,
  Text,
} from "@chakra-ui/react";
import { SetStateAction, useState } from "react";
import { useDispatch } from "react-redux";
import useUserProfile from "../../hooks/useUserProfile";
import {
  useCreateOrderEntriesMutation,
  useCreateOrderLocationMutation,
  useCreateOrderMutation,
  useCreateOrderUserMutation,
  useDeleteOrderMutation,
  useSendInvoiceMutation,
} from "../../store/api/order-api-slice";
import { CartSliceState } from "../../store/cart/cartSlice";
import { OrderData } from "./CartConfirmation";
import startOrder from "./startOrder";

type ConfirmOrderModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  changeCurrentCartStep: (value: SetStateAction<number>) => void;
  orderData: OrderData;
  orderDetails: CartSliceState;
};

const ConfirmOrderModal = ({
  isOpen,
  setIsOpen,
  changeCurrentCartStep,
  orderData,
  orderDetails,
}: ConfirmOrderModalProps) => {
  const [isSaving, setIsSaving] = useState(false);
  const userProfile = useUserProfile();

  const [createLocation] = useCreateOrderLocationMutation();
  const [createUser] = useCreateOrderUserMutation();
  const [createOrder] = useCreateOrderMutation();
  const [createOrderEntries] = useCreateOrderEntriesMutation();
  const [sendInvoice] = useSendInvoiceMutation();
  const [deleteOrder] = useDeleteOrderMutation();
  const dispatch = useDispatch();

  const onSubmitHandler = () => {
    startOrder(
      createLocation,
      createUser,
      createOrder,
      createOrderEntries,
      sendInvoice,
      deleteOrder,
      dispatch,
      setIsSaving,
      setIsOpen,
      changeCurrentCartStep,
      orderData,
      orderDetails.deliveryPickupLocation,
      orderDetails.cartItems,
      userProfile?.username!
    );
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={() => setIsOpen(false)}
      isCentered
      closeOnOverlayClick={false}
    >
      <ModalOverlay backdropFilter="blur(2px)" />
      <ModalContent>
        <ModalHeader>Place order</ModalHeader>
        <ModalCloseButton disabled={isSaving} />
        <ModalBody>
          <Text>Are you sure you want to place your order?</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            mr={3}
            onClick={onSubmitHandler}
            disabled={isSaving}
          >
            {!isSaving ? <Text>Confirm</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="gray"
            onClick={() => setIsOpen(false)}
            disabled={isSaving}
          >
            {!isSaving ? <Text>Cancel</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default ConfirmOrderModal;
