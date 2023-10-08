import { Box, Text, VStack} from "@chakra-ui/react";
import parse from "html-react-parser";

export interface ProfileDescriptionProps {
    description: string,
};

const ProfileDescription: React.FC<ProfileDescriptionProps> = ({description}) => {
    return (
    <VStack px="16px" py="8px" bg="purple.50" w="100%">
        <Text color="primary.400" textStyle="h3" fontSize="16px" userSelect="none" alignSelf="start">ABOUT ME</Text>
        <Box textStyle="p" alignSelf="start">{parse(description !== "" ? description : "Tell the community a little about yourself.")}</Box>         
    </VStack>
    )
}

export default ProfileDescription;