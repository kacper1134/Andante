import {
  Avatar,
  Icon,
  Text,
  HStack,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  ModalFooter,
  Button,
  Input,
  VStack,
  Center,
  useToast,
} from "@chakra-ui/react";
import { useEffect, useRef, useState } from "react";
import { AiFillCamera } from "react-icons/ai";
import { RiImageAddFill } from "react-icons/ri";
import { imageSize, normalTextFontSize } from "./ProfileEditSizes";
import { useTranslation } from "react-i18next";

type ProfileEditImageProps = {
  picture: File | null | undefined;
  setPicture: React.Dispatch<React.SetStateAction<File | null | undefined>>;
  inititalPictureUrl: string;
};

const ProfileEditImage = ({
  picture,
  setPicture,
  inititalPictureUrl,
}: ProfileEditImageProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const hiddenFileInput = useRef<HTMLInputElement>(null);
  const [currentPicture, setCurrentPicture] = useState<File | null | undefined>(
    picture
  );
  const [newImageUrl, setNewImageUrl] = useState("");

  useEffect(() => {
    setCurrentPicture(picture);
  }, [picture]);

  const toast = useToast();
  const {t} = useTranslation();
  const onSubmit = () => {
    if (!(currentPicture && currentPicture.type.match("image/*"))) {
      toast({
        title: t("profilePage.edit.image.toast.error.title"),
        description: t("profilePage.edit.image.toast.error.content"),
        status: "error",
        isClosable: true,
      });
      return;
    }
    setIsOpen(false);

    setPicture(currentPicture);
  };

  const onClose = () => {
    setIsOpen(false);
    setCurrentPicture(picture);
  };
  
  return (
    <VStack>
      <Avatar
        borderRadius="full"
        boxSize={imageSize}
        src={picture ? newImageUrl : inititalPictureUrl}
      />
      <HStack
        color="white"
        bg="primary.400"
        fontSize={normalTextFontSize}
        borderRadius="xl"
        p="7px"
        cursor="pointer"
        onClick={() => setIsOpen(true)}
      >
        <Text>{t("profilePage.edit.buttons.edit")}</Text>
        <Icon as={AiFillCamera} />
      </HStack>
      <Modal
        isOpen={isOpen}
        onClose={onClose}
        isCentered
        closeOnOverlayClick={false}
      >
        <ModalOverlay backdropFilter="blur(2px)" />
        <ModalContent>
          <ModalHeader>{t("profilePage.edit.image.title")}</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Input
              name="picture"
              id="picture"
              type="file"
              accept="image/*"
              placeholder="Enter a picture url"
              onChange={(e) => {
                setNewImageUrl(URL.createObjectURL(e.target.files?.item(0)!));
                setCurrentPicture(e.target.files?.item(0));
              }}
              display="none"
              ref={hiddenFileInput}
            />
            <HStack as={Center}>
              <Button
                onClick={() => {
                  hiddenFileInput.current?.click();
                }}
                colorScheme="primary"
                w="100%"
              >
                {t("profilePage.edit.image.content")} <Icon as={RiImageAddFill} ml="10px" />
              </Button>
              <Text>{currentPicture?.name}</Text>
            </HStack>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="primary" mr={3} onClick={onSubmit}>
            {t("profilePage.edit.image.confirm")}
            </Button>
            <Button colorScheme="gray" onClick={onClose}>
            {t("profilePage.edit.image.cancel")}
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </VStack>
  );
};

export default ProfileEditImage;
