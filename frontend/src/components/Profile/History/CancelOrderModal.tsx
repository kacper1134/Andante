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
import { useTranslation } from "react-i18next";

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
  const {t} = useTranslation();
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
          t("profilePage.history.modal.toast.success.title"),
          "success",
          t("profilePage.history.modal.toast.success.content")
        );
      })
      .catch(() => {
        setSaving(false);
        showToast(t("profilePage.history.modal.toast.error.title"), "error", t("profilePage.history.modal.toast.error.content"));
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
        <ModalHeader>{t("profilePage.history.modal.title")}</ModalHeader>
        <ModalCloseButton disabled={saving} />
        <ModalBody>
          <Text>
            {t("profilePage.history.modal.content")} {order.orderId}?
          </Text>
          <Text as="sub">{t("profilePage.history.modal.subcontent")}</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            mr={3}
            disabled={saving}
            onClick={onSubmit}
          >
            {!saving ? <Text>{t("profilePage.history.modal.confirm")}</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="gray"
            onClick={() => setIsOpen(false)}
            disabled={saving}
          >
            {!saving ? <Text>{t("profilePage.history.modal.cancel")}</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default CancelOrderModal;
