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
  useToast,
} from "@chakra-ui/react";
import { useState } from "react";
import { useUpdateOrderMutation } from "../../../store/api/order-api-slice";
import { OrderDetails } from "./OrdersList";

type CancelOrderModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setReload: React.Dispatch<React.SetStateAction<boolean>>;
  order: OrderDetails;
};

const CancelOrderModal = ({
  isOpen,
  setIsOpen,
  setReload,
  order,
}: CancelOrderModalProps) => {
  const [saving, setSaving] = useState(false);
  const [updateOrder] = useUpdateOrderMutation();
  const toast = useToast();

  const onSubmit = () => {
    setSaving(true);
    updateOrder({
      id: order.orderId,
      deliveryCost: order.deliveryCost!,
      deliveryMethod: order.deliveryMethod!,
      paymentMethod: order.paymentMethod!,
      clientEmail: order.addressedDetails.email,
      clientId: order.addressedDetails.id!,
      locationId: order.locationId!,
      deliveryLocationId: order.deliveryLocationId!,
      status: "Canceled",
      paymentCost: order.paymentCost!,
      totalCost: order.totalCost!,
      orderEntriesIds: order.orderedItems.map((item) => item.variant.id),
    })
      .then(() => {
        setSaving(false);
        setIsOpen(false);
        setReload(true);
        showToast(
          "Cancel order",
          "success",
          "Order was successfully canceled!"
        );
      })
      .catch(() => {
        setSaving(false);
        showToast("Cancel order", "error", "Unexpected error");
      });
  };

  const showToast = (
    title: string,
    status: "success" | "error",
    message: string
  ) => {
    toast({
      title: title,
      description: message,
      status: status,
      isClosable: true,
    });
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
        <ModalHeader>Cancel your order</ModalHeader>
        <ModalCloseButton disabled={saving} />
        <ModalBody>
          <Text>
            Are you sure you want to cancel order with id {order.orderId}?
          </Text>
          <Text as="sub">Please note that this action is irreversible!</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            mr={3}
            disabled={saving}
            onClick={onSubmit}
          >
            {!saving ? <Text>Confirm</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="gray"
            onClick={() => setIsOpen(false)}
            disabled={saving}
          >
            {!saving ? <Text>Cancel</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default CancelOrderModal;
