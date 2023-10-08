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
        writeClient.delete(assetRef).catch(() => {});
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
            Are you sure you want to delete blog post with title {title}?
          </Text>
          <Text as="sub">Please note that this action is irreversible!</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="primary"
            onClick={() => onSubmit()}
            mr={3}
            disabled={saving}
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

export default BlogDeleteModal;
