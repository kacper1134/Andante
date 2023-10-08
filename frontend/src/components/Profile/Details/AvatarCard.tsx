import { Image, Flex, VStack, Box, Text, Avatar, Center } from '@chakra-ui/react';
import { useSelector } from 'react-redux';
import useUserProfile from '../../../hooks/useUserProfile';
import { RootState } from '../../../store';
import { UserDetails } from '../../../utils/KeycloakUtils';
import parse from "html-react-parser";
import useGetFirebaseImage from '../../../hooks/useGetFirebaseImage';
import { useEffect, useState } from 'react';
import { useKeycloak } from '@react-keycloak/web';

export interface AvatarCardProps {
    borderRadius: string,
    fontSize: number,
    height: string,
    width: string,
};

const AvatarCard: React.FC<AvatarCardProps> = ({borderRadius, fontSize, height, width}) => {
    const userDetails: UserDetails | undefined = useSelector((state: RootState) => state.auth.userDetails);    
    const userProfile = useUserProfile();
    const {initialized, keycloak} = useKeycloak();

    const getImage = useGetFirebaseImage();
    const [image, setImage] = useState("");
    
    useEffect(() => {
        if(initialized && keycloak.authenticated && userProfile?.imageUrl !== undefined) {
            getImage(userProfile?.imageUrl ?? "")
            .then((url) => setImage(url))
            .catch(() => setImage(""));
        }
    }, [initialized, getImage, userProfile?.imageUrl, keycloak.authenticated])

    return (
        <Flex w={width} h={height}>
            <Box minWidth={height} minHeight={height} position="sticky" top="0">
                <Image src={image} w={height} h={height} objectFit="cover" objectPosition="top" borderRadius={borderRadius}
                fallback={<Center h="100%"><Avatar size="xl" /></Center>} />
            </Box>
            <VStack flexGrow={1} ps="16px">
                <Text textStyle="h2" fontSize={`${fontSize}px`} color="primary.800" alignSelf="start" position="sticky" top="0">{userDetails?.personal.name ?? "Unknown"} {userDetails?.personal.surname ?? "Unknown"}</Text>
                <Box textStyle="p"  fontSize={`${fontSize - 2}px`} color="gray.500" alignSelf="start" overflowY="scroll">{parse(userDetails?.personal.description ?? "Not provided")}</Box>
            </VStack>
        </Flex> 

    )
}

export default AvatarCard;