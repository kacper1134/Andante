import {
  Button,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { Domain } from "../../store/api/result/dto/activity/ActivityDTO";

type NotificationModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  type: Domain;
  relatedId: string;
};

const NotificationModal = ({
  isOpen,
  setIsOpen,
  type,
  relatedId,
}: NotificationModalProps) => {
  const navigate = useNavigate();
  const onConfirmHandler = () => {
    if (type === Domain.PRODUCT)
      navigate("../shop/product/" + relatedId);
    if (type === Domain.FORUM)
      navigate("../forum/post/" + relatedId);
    if (type === Domain.ORDER) navigate("../profile/orders");
  };

  return (
    <Modal
      closeOnOverlayClick={false}
      isCentered
      isOpen={isOpen}
      size="md"
      onClose={() => setIsOpen(false)}
    >
      <ModalOverlay backdropFilter="blur(3px)" />
      <ModalContent bg="primary.50" rounded="2xl" textStyle="p">
        <ModalHeader textStyle="h3">Navigate to {type.toLowerCase()}</ModalHeader>
        <ModalCloseButton />
        <ModalBody pb={6}>
          After confirmation, you will be taken to the {type.toLowerCase()} page.
        </ModalBody>
        <ModalFooter>
          <Button colorScheme="primary" mr="10px" onClick={onConfirmHandler}>
            Confirm
          </Button>
          <Button colorScheme="gray" onClick={() => setIsOpen(false)}>
            Cancel
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default NotificationModal;
