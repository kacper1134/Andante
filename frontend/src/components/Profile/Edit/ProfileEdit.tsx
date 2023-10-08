import {
  HStack,
  VStack,
  Text,
  Center,
  SimpleGrid,
  FormControl,
  FormLabel,
  Input,
  Select,
  Spacer,
  Button,
  Box,
} from "@chakra-ui/react";
import { useCallback, useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../../store";
import UserTextEditor from "../../common/UserTextEditor";
import FormEditInput from "./FormEditInput";
import ProfileEditImage from "./ProfileEditImage";
import { headerFontSize, normalTextFontSize } from "./ProfileEditSizes";
import UpdateProfileModal from "./UpdateProfileModal";
import { useUpdateProfileMutation } from "../../../store/api/profile-api-slice";
import { useKeycloak } from "@react-keycloak/web";
import useUserProfile from "../../../hooks/useUserProfile";
import useGetFirebaseImage from "../../../hooks/useGetFirebaseImage";

export type ProfileEditType = {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  dateOfBirth: string;
  country: string;
  city: string;
  street: string | null;
  postalCode: string | null;
  gender: string;
  profileImageUrl: string;
  description: string;
};

export type ProfileType = {
  key: string;
  username: string;
  imageUrl: string;
  observed: ProfileType[];
  observers: ProfileType[];
};

const formatDate = (date: Date | undefined) => {
  let dateToFormat = date;
  if (!dateToFormat) return "";
  if (dateToFormat >= new Date(Date.now())) {
    dateToFormat = new Date(Date.now());
  }
  return (
    dateToFormat.getFullYear() +
    "-" +
    dateToFormat.toLocaleString("default", { month: "2-digit" }) +
    "-" +
    dateToFormat.getDate().toString().padStart(2, "0")
  );
};

const ProfileEdit = () => {
  const userData = useSelector((state: RootState) => state.auth.userDetails);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phonenumber, setPhonenumber] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const [country, setCountry] = useState("");
  const [city, setCity] = useState("");
  const [street, setStreet] = useState("");
  const [postalcode, setPostalcode] = useState("");
  const [gender, setGender] = useState("");
  const [description, setDescription] = useState("");
  const [updateProfile] = useUpdateProfileMutation();
  const userProfile = useUserProfile();
  const [picture, setPicture] = useState<File | null>();
  const [inititalPictureUrl, setInititalPictureUrl] = useState("");

  const getImage = useGetFirebaseImage();

  const onCancel = useCallback(() => {
    setFirstName(userData?.personal.name ?? "");
    setLastName(userData?.personal.surname ?? "");
    setPhonenumber(userData?.personal.phoneNumber ?? "");
    setDateOfBirth(
      formatDate(new Date(Date.parse(userData?.personal.dateOfBirth as any)))
    );
    setCountry(userData?.delivery.country ?? "");
    setCity(userData?.delivery.city ?? "");
    setStreet(userData?.delivery.street ?? "");
    setPostalcode(userData?.delivery.postalCode ?? "");
    setGender(userData?.personal.gender ?? "");
    setDescription(userData?.personal.description ?? "Not provided");
    setPicture(null);
  }, [userData]);

  const { keycloak } = useKeycloak();

  useEffect(() => {
    onCancel();
  }, [onCancel]);

  useEffect(() => {
    if (userProfile?.imageUrl !== undefined && userProfile?.imageUrl !== "") {
      getImage(userProfile.imageUrl)
        .then((url) => setInititalPictureUrl(url))
        .catch(() => setInititalPictureUrl(""));
    }
  }, [keycloak.authenticated, userProfile?.imageUrl]);

  const updateProfileHandler = (imageUrl: string) => {
    updateProfile({
      firstName,
      lastName,
      phoneNumber: phonenumber,
      dateOfBirth,
      country,
      city,
      street: street === "" ? null : street,
      postalCode: postalcode === "" ? null : postalcode,
      gender,
      profileImageUrl: imageUrl,
      description,
    }).then(() => {
      keycloak.login();
    });
  };

  const [isUpdateModal, setIsUpdateModal] = useState(false);

  return (
    <HStack h="100%" w="100%">
      <Center w="100%" h="100%">
        <VStack
          w="30%"
          my="5%"
          mr="1%"
          h="90%"
          pb="10px"
          textAlign="center"
          boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
          fontSize={normalTextFontSize}
        >
          <ProfileEditImage
            picture={picture}
            setPicture={setPicture}
            inititalPictureUrl={inititalPictureUrl}
          />
          <Text textStyle="h2" px="3%" color="primary.400">
            {userData?.personal.name} {userData?.personal.surname}
          </Text>
          <Text textStyle="h1" fontSize={headerFontSize} color="primary.600">
            About
          </Text>
          <UserTextEditor
            initialValue={userData?.personal.description}
            padding="3%"
            content={description}
            setContent={setDescription}
          />
          <Spacer />
        </VStack>
        <VStack
          h="90%"
          w="65%"
          boxShadow="0 4px 4px 0 rgba(0,0,0,0.25)"
          pb="10px"
        >
          <VStack alignItems="flex-start" w="95%">
            <Text textStyle="h1" fontSize={headerFontSize} color="primary.600">
              Personal Details
            </Text>
            <SimpleGrid columns={2} spacingX={10} spacingY={4} w="100%">
              <FormEditInput
                label="Name*"
                placeholder="Enter your name"
                value={firstName}
                setValue={setFirstName}
              />
              <FormEditInput
                label="Surname*"
                placeholder="Enter your surname"
                value={lastName}
                setValue={setLastName}
              />
              <FormEditInput
                label="Phone*"
                placeholder="Enter your phone number"
                value={phonenumber}
                setValue={setPhonenumber}
              />
              <FormControl w="100%">
                <FormLabel
                  fontSize={normalTextFontSize}
                  textStyle="h1"
                  fontWeight="semibold"
                >
                  Birthdate*
                </FormLabel>
                <Input
                  fontSize={normalTextFontSize}
                  textStyle="p"
                  type="date"
                  value={dateOfBirth}
                  onChange={(event) => setDateOfBirth(event.target.value)}
                  max={new Date().toISOString().slice(0, 10)}
                />
              </FormControl>
              <FormControl w="100%">
                <FormLabel
                  fontSize={normalTextFontSize}
                  textStyle="h1"
                  fontWeight="semibold"
                >
                  Gender*
                </FormLabel>
                <Select
                  textStyle="p"
                  fontSize={normalTextFontSize}
                  value={gender}
                  onChange={(event) => setGender(event.target.value)}
                >
                  <option value="Female">Female</option>
                  <option value="Male">Male</option>
                </Select>
              </FormControl>
            </SimpleGrid>
          </VStack>
          <Spacer />
          <VStack alignItems="flex-start" w="95%">
            <Text textStyle="h1" fontSize={headerFontSize} color="primary.600">
              Delivery
            </Text>
            <SimpleGrid columns={2} spacingX={10} spacingY={4} w="100%">
              <FormEditInput
                label="Country*"
                placeholder="Enter your country"
                value={country}
                setValue={setCountry}
              />
              <FormEditInput
                label="City*"
                placeholder="Enter your city"
                value={city}
                setValue={setCity}
              />
              <FormEditInput
                label="Street"
                placeholder="Enter your street"
                value={street}
                setValue={setStreet}
              />
              <FormEditInput
                label="Postal Code"
                placeholder="Enter your postal code"
                value={postalcode}
                setValue={setPostalcode}
              />
            </SimpleGrid>
          </VStack>
          <Spacer />
          <HStack w="100%">
            <Spacer />
            <Box>
              <Button
                colorScheme="gray"
                mr="10px"
                textStyle="p"
                fontSize={normalTextFontSize}
                h="fit-content"
                py="10px"
                onClick={() => onCancel()}
              >
                Cancel
              </Button>
              <Button
                colorScheme="primary"
                textStyle="p"
                fontSize={normalTextFontSize}
                h="fit-content"
                py="10px"
                mr="5px"
                onClick={() => setIsUpdateModal(true)}
              >
                Update
              </Button>
            </Box>
          </HStack>
        </VStack>
      </Center>
      <UpdateProfileModal
        isOpen={isUpdateModal}
        setIsOpen={setIsUpdateModal}
        updateProfile={updateProfileHandler}
        userData={{
          firstName,
          lastName,
          phoneNumber: phonenumber,
          dateOfBirth,
          country,
          city,
          street,
          postalCode: postalcode,
          gender,
          profileImageUrl: "",
          description,
        }}
        picture={picture}
      />
    </HStack>
  );
};

export default ProfileEdit;
