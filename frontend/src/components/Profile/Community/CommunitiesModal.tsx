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
import { useKeycloak } from "@react-keycloak/web";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useJoinChatMutation } from "../../../store/api/public-chat-api-slice";
import { getUserDetails } from "../../../utils/KeycloakUtils";

type CommunitiesModalProps = {
  chatId: number;
  name: string;
  isOpen: boolean;
  setModalState: React.Dispatch<
    React.SetStateAction<{
      isModalOpen: boolean;
      chatId: number;
      name: string;
    }>
  >;
};

const CommunitiesModal = ({
  chatId,
  name,
  isOpen,
  setModalState,
}: CommunitiesModalProps) => {
  const navigate = useNavigate();
  const { keycloak } = useKeycloak();
  const details = keycloak.idTokenParsed ? getUserDetails(keycloak.idTokenParsed!) : undefined;
  const [joinChat] = useJoinChatMutation();

  const joinGroupHandler = (communityId: number) => {
    setIsLoading(true);
    joinChat({
      chatId: communityId,
      body: { username: details!.personal.username },
    }).then(() => {
      navigate("/chat");
    });
  };

  const [isLoading, setIsLoading] = useState(false);

  const onCloseHandler = () => {
    setModalState({ isModalOpen: false, chatId: 0, name: "" });
  };

  return (
    <Modal
      closeOnOverlayClick={false}
      isCentered
      isOpen={isOpen}
      size="lg"
      onClose={onCloseHandler}
    >
      <ModalOverlay backdropFilter="blur(3px)" />
      <ModalContent bg="primary.50" rounded="2xl">
        <ModalHeader>Join public chat group</ModalHeader>
        {!isLoading && <ModalCloseButton color="red" />}
        <ModalBody pb={6}>
          {isLoading && <Spinner color="primary.600" />}
          {!isLoading && (
            <Text>Do you want to join public group named {name}?</Text>
          )}
        </ModalBody>

        {!isLoading && (
          <ModalFooter>
            <Button
              colorScheme="primary"
              mr={3}
              onClick={() => joinGroupHandler(chatId)}
            >
              Confirm
            </Button>
            <Button onClick={onCloseHandler}>Cancel</Button>
          </ModalFooter>
        )}
      </ModalContent>
    </Modal>
  );
};

export default CommunitiesModal;
