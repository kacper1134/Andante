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
import { deleteObject, ref, uploadBytes } from "firebase/storage";
import { useState } from "react";
import storage from "../../../config/firebase-config";
import useUserProfile from "../../../hooks/useUserProfile";

type UpdateProfileModalProps = {
  isOpen: boolean;
  userData: {
    firstName: string;
    lastName: string;
    phoneNumber: string;
    dateOfBirth: string;
    country: string;
    city: string;
    street: string | undefined;
    postalCode: string | undefined;
    gender: string;
    profileImageUrl: string;
    description: string;
  };
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  updateProfile: (imageUrl: string) => void;
  picture: File | null | undefined;
};

export const createGUID = () => {
  function S4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
  }

  return (
    S4() +
    S4() +
    "-" +
    S4() +
    "-4" +
    S4().substring(0, 3) +
    "-" +
    S4() +
    "-" +
    S4() +
    S4() +
    S4()
  ).toLowerCase();
};

const UpdateProfileModal = ({
  isOpen,
  setIsOpen,
  updateProfile,
  userData,
  picture,
}: UpdateProfileModalProps) => {
  const toast = useToast();
  const userProfile = useUserProfile();
  const [saving, setSaving] = useState(false);

  const onSubmit = async () => {
    if(!checkValidity()) return;
    let currentImageProfilePath: string | undefined = userProfile?.imageUrl;
    let profilePath: string | undefined = "";
    setSaving(true);
    if (picture && picture !== undefined) {
      profilePath = await createImage();
      if (profilePath === undefined || profilePath === "") {
        setSaving(false);
        return;
      }
      if (currentImageProfilePath != undefined && currentImageProfilePath !== "") {
        await deleteImage(currentImageProfilePath!);
      }
    } else {
      profilePath = currentImageProfilePath;
    }

    setIsOpen(false);
    setSaving(false);
    toast({
      title: "Update Account",
      description: "Your account has been updated",
      status: "success",
      isClosable: true,
    });
    updateProfile(profilePath!);
  };

  const createImage = async () => {
    const profileImagePath =
      "profile/" + createGUID() + "." + picture?.name.split(".").pop();
    const profileImageRef = ref(storage, profileImagePath);
    const result = await uploadBytes(profileImageRef, picture!).catch(() => {
      toast({
        title: "Update Account",
        description: "Your profile image size is too big, maximum is 5MB!",
        status: "error",
        isClosable: true,
      });
      setIsOpen(false);
      return undefined;
    });
    if (result === undefined) return undefined;
    return profileImagePath;
  };

  const deleteImage = async (currentImageProfilePath: string) => {
    const profileImageRef = ref(storage, currentImageProfilePath);
    deleteObject(profileImageRef).catch(() => {});
  };

  const checkValidity = () => {
    if(!(userData.firstName && userData.firstName.length >= 2 && userData.firstName.length <= 100)) {
      showToast("Update Account", "error", `User first name '${userData.firstName}' must be between 2 and 100 characters long`);
      return false;
    }
    if(!(userData.lastName && userData.lastName.length >= 2 && userData.lastName.length <= 100)) {
      showToast("Update Account", "error", `User last name '${userData.lastName}' must be between 2 and 100 characters long`);
      return false;
    }
    if(!(userData.phoneNumber && userData.phoneNumber.match("^[0-9]{9}"))) {
      showToast("Update Account", "error", `User phone number '${userData.phoneNumber}' must be exactly nine digits long`);
      return false;
    }
    if(!(userData.dateOfBirth && userData.dateOfBirth.length > 0)) {
      showToast("Update Account", "error", `User date of birth '${userData.dateOfBirth}' must not be blank`);
      return false;
    }
    if(!(userData.country && userData.country.length >= 2 && userData.country.length <= 100)) {
      showToast("Update Account", "error", `User country '${userData.country}' must be between 2 and 100 characters long`);
      return false;
    }
    if(!(userData.city && userData.city.length >= 2 && userData.city.length <= 150)) {
      showToast("Update Account", "error", `User city '${userData.city}' must be between 2 and 150 characters long`);
      return false;
    }
    if(!(userData.street && userData.street.length >= 2 && userData.street.length <= 150) && userData.street !== "") {
      showToast("Update Account", "error", `User street '${userData.street}' must be between 2 and 150 characters long`);
      return false;
    }
    if(!(userData.postalCode && userData.postalCode.match("^[0-9]{2}-[0-9]{3}")) && userData.postalCode !== "") {
      showToast("Update Account", "error", `User postal code '${userData.postalCode}' must be in XX-XXX format`);
      return false;
    }
    if(!(userData.gender && userData.gender.length > 0)) {
      showToast("Update Account", "error", `User gender '${userData.gender}' must not be blank`);
      return false;
    }
    return true;
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
        <ModalHeader>Update profile</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <Text>
            Are you sure you want to change the information for your account?
          </Text>
          <Text as="sub">Please note that this action is irreversible!</Text>
        </ModalBody>
        <ModalFooter>
          <Button
            colorScheme="gray"
            mr={3}
            onClick={() => setIsOpen(false)}
            disabled={saving}
          >
            {!saving ? <Text>Cancel</Text> : <Spinner />}
          </Button>
          <Button
            colorScheme="primary"
            onClick={() => onSubmit()}
            disabled={saving}
          >
            {!saving ? <Text>Confirm</Text> : <Spinner />}
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default UpdateProfileModal;
