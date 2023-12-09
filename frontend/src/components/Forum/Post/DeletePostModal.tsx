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
import { useNavigate, useParams } from "react-router-dom";
import { useDeletePostMutation } from "../../../store/api/forum-api-slice";
import { useTranslation } from "react-i18next";

type DeletePostModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  title: string;
  topicId: number;
};

const DeletePostModal = ({
  isOpen,
  setIsOpen,
  title,
  topicId,
}: DeletePostModalProps) => {
  const toast = useToast();
  const [saving, setSaving] = useState(false);
  const { id } = useParams();
  const [deletePost] = useDeletePostMutation();
  const navigate = useNavigate();

  const onSubmit = () => {
    setSaving(true);
    deletePost({ topicId: topicId, postId: +id! })
      .then(() => {
        navigate(`../topic/${topicId}`);
        toast({
          title: "Delete post",
          description: "Your post was deleted successfully",
          status: "success",
          isClosable: true,
          duration: 2000,
        });
      })
      .finally(() => setSaving(false));
  };

  const { t } = useTranslation();

  return (
    <Modal
      isOpen={isOpen}
      onClose={() => setIsOpen(false)}
      isCentered
      closeOnOverlayClick={false}
    >
      <ModalOverlay backdropFilter="blur(2px)" />
      <ModalContent>
        <ModalHeader>{t("forum-section.delete-forum-post")}</ModalHeader>
        <ModalCloseButton disabled={saving} />
        <ModalBody>
          <Text>
            {t("forum-section.delete-post-confirmation")} {title}?
          </Text>
          <Text as="sub">{t("forum-section.delete-irreversible")}</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            onClick={() => onSubmit()}
            mr={3}
            disabled={saving}
          >
            {!saving ? <Text>{t("forum-section.delete-confirm")}</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="gray"
            onClick={() => setIsOpen(false)}
            disabled={saving}
          >
            {!saving ? <Text>{t("forum-section.delete-cancel")}</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default DeletePostModal;
