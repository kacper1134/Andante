import { VStack, Text, Wrap, WrapItem, Avatar, Tooltip, useToken, Icon} from "@chakra-ui/react";
import { FaUserCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { UserProfileDTO } from "../../../store/api/result/dto/activity/UserProfileDTO";

export interface FollowingProps {
    users: UserProfileDTO[],
    usersImages: Map<string, string>,
}

const Following: React.FC<FollowingProps> = ({users, usersImages}) => {
    const followingCount = users.length;
    const primary400 = useToken("colors", "primary.400");
    const navigate = useNavigate();
    
    return <VStack w="100%" px="16px" py="8px" bg="purple.50">
        <Text color="primary.400" textStyle="h3" fontSize="16px" userSelect="none" alignSelf="start">FOLLOWING: {followingCount}</Text>
        <Wrap alignSelf="start" spacing="24px" px="24px">
            {users.map((user, index) =>
            <Tooltip label={user.username} bg="primary.400" key={index}>
                {usersImages.get(user.username) ? <WrapItem as={Avatar} src={usersImages.get(user.username)} size="lg" border={`1px solid ${primary400}`}
                    onClick={() => navigate(`/profile/community/${user.username}`)}
                    cursor="pointer"/> : 
                <Icon
                    boxSize="62px"
                    onClick={() => navigate(`/profile/community/${user.username}`)}
                    cursor="pointer"
                    as={FaUserCircle}
                />}
            </Tooltip>
            )}
        </Wrap>
    </VStack>
}

export default Following;