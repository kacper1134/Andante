import {
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Text,
  Button,
  useToast,
  Spinner,
} from "@chakra-ui/react";
import { useState } from "react";
import { writeClient } from "../../../client";
import { useTranslation } from "react-i18next";

type BlogDeleteModalProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  title: string;
  id: string;
  setUpdated: React.Dispatch<React.SetStateAction<boolean>>;
  image: any;
};

const BlogDeleteModal = ({
  isOpen,
  setIsOpen,
  setUpdated,
  title,
  id,
  image,
}: BlogDeleteModalProps) => {
  const assetRef = image.asset._ref;
  const toast = useToast();
  const [saving, setSaving] = useState(false);

  const onSubmit = () => {
    setSaving(true);
    writeClient
      .delete(id)
      .then(() => {
        setIsOpen(false);
        setUpdated(true);
        showToast("Delete post", "success", "Post was deleted successfully!");
        writeClient.delete(assetRef).catch(() => { });
        const deleteCommentsQuery = `*[_type == "comment" && postTitle == "${title}"]`;
        writeClient.delete({ query: deleteCommentsQuery });
      })
      .catch(() => {
        setSaving(false);
        showToast("Delete post", "error", "Unexpected error");
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
      duration: 2000,
    });
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
        <ModalHeader>Delete blog post</ModalHeader>
        <ModalCloseButton disabled={saving} />
        <ModalBody>
          <Text>
            {t("blog-section.delete-blog-post")} {title}?
          </Text>
          <Text as="sub">{t("blog-section.delete-irreversible")}</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            onClick={() => onSubmit()}
            mr={3}
            disabled={saving}
          >
            {!saving ? <Text>{t("blog-section.delete-confirm")}</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="gray"
            onClick={() => setIsOpen(false)}
            disabled={saving}
          >
            {!saving ? <Text>{t("blog-section.delete-cancel")}</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default BlogDeleteModal;
