import {
  Button,
  Center,
  HStack,
  Icon,
  Input,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Text,
  useToast,
} from "@chakra-ui/react";
import { deleteObject, ref, uploadBytes } from "firebase/storage";
import { useRef, useState } from "react";
import { RiImageAddFill } from "react-icons/ri";
import storage from "../../../config/firebase-config";
import { useUpdateCommunityImageMutation } from "../../../store/api/profile-api-slice";
import { createGUID } from "../Edit/UpdateProfileModal";

type CommunityEditImageProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setReloadImage: React.Dispatch<React.SetStateAction<boolean>>
  inititalPictureUrl: string;
};

const CommunityEditImage = ({
  inititalPictureUrl,
  setReloadImage,
  isOpen,
  setIsOpen,
}: CommunityEditImageProps) => {
  const hiddenFileInput = useRef<HTMLInputElement>(null);
  const [currentPicture, setCurrentPicture] = useState<File | null | undefined>();
  const [updateCommunityImage] = useUpdateCommunityImageMutation();
  const [saving, setSaving] = useState(false);
  const toast = useToast();

  const onSubmit = async () => {
    if (!(currentPicture && currentPicture.type.match("image/*"))) {
      toast({
        title: "Upload image",
        description: "Uploaded file is not an image!",
        status: "error",
        isClosable: true,
      });
      return;
    }

    let currentImageProfilePath: string | undefined = inititalPictureUrl;
    let imagePath: string | undefined = "";

    if (currentPicture && currentPicture !== undefined) {
      imagePath = await createImage();
      if (imagePath === undefined || imagePath === "") {
        setSaving(false);
        return;
      }
      if (currentImageProfilePath !== "") {
        await deleteImage(currentImageProfilePath!);
      }
    }

    if (imagePath !== "") {
      setIsOpen(false);
      updateCommunityImage({ imagePath: imagePath }).then(() => setReloadImage(true));
    }
  };

  const onClose = () => {
    setIsOpen(false);
  };

  const createImage = async () => {
    const profileImagePath =
      "profile/" + createGUID() + "." + currentPicture?.name.split(".").pop();

    const profileImageRef = ref(storage, profileImagePath);
    const result = await uploadBytes(profileImageRef, currentPicture!).catch(
      () => {
        toast({
          title: "Update Account",
          description: "Your profile image size is too big, maximum is 5MB!",
          status: "error",
          isClosable: true,
        });
        setIsOpen(false);
        return undefined;
      }
    );
    if (result === undefined) return undefined;
    return profileImagePath;
  };

  const deleteImage = async (currentImageProfilePath: string) => {
    const profileImageRef = ref(storage, currentImageProfilePath);
    deleteObject(profileImageRef).catch(() => {});
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      isCentered
      closeOnOverlayClick={false}
    >
      <ModalOverlay backdropFilter="blur(2px)" />
      <ModalContent>
        <ModalHeader>Edit community image</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Input
            name="picture"
            id="picture"
            type="file"
            accept="image/*"
            placeholder="Enter a picture url"
            onChange={(e) => {
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
              Upload a picture <Icon as={RiImageAddFill} ml="10px" />
            </Button>
            <Text>{currentPicture?.name}</Text>
          </HStack>
        </ModalBody>
        <ModalFooter>
          <Button colorScheme="primary" mr={3} onClick={onSubmit}>
            Confirm
          </Button>
          <Button colorScheme="gray" onClick={onClose}>
            Cancel
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default CommunityEditImage;
