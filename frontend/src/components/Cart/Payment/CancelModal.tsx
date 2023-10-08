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
import { useState } from "react";
import { useNavigate } from "react-router-dom";

type CancelModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const CancelModal = ({ isOpen, setIsOpen }: CancelModalProps) => {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  return (
    <Modal
      closeOnOverlayClick={false}
      isCentered
      isOpen={isOpen}
      size="md"
      onClose={() => setIsOpen(false)}
    >
      <ModalOverlay backdropFilter="blur(3px)" />
      <ModalContent bg="primary.50" rounded="2xl">
        <ModalHeader textStyle="h3">Cancel your order</ModalHeader>
        {!isLoading && <ModalCloseButton />}
        <ModalBody pb={6}>
          {isLoading && <Spinner color="primary.600" />}
          {!isLoading && (
            <>
              <Text fontSize="18px" textStyle="p">Do you want to cancel your order?</Text>{" "}
              <Text fontSize="14px" color="gray" textStyle="p">Note: this actions is irreversable</Text>
            </>
          )}
        </ModalBody>

        {!isLoading && (
          <ModalFooter>
            <Button
              colorScheme="primary"
              mr={3}
              onClick={() => {
                setIsLoading(true);
                navigate("/shop");
              }}
            >
              Confirm
            </Button>
            <Button onClick={() => setIsOpen(false)} color="white" bg="purple.300" _hover={{bg: "purple.400"}} _active={{bg: "purple.400"}}>Cancel</Button>
          </ModalFooter>
        )}
      </ModalContent>
    </Modal>
  );
};

export default CancelModal;
