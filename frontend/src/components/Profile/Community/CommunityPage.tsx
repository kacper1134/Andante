import { Center, Divider, Spinner, useToast, VStack } from "@chakra-ui/react";
import ProfileBanner from "./ProfileBanner";
import meze from "../../../static/meze.png";
import ProfileDescription from "./ProfileDescription";
import { UserDetails } from "../../../utils/KeycloakUtils";
import TopPosts from "./TopPosts";
import Communities, { Community } from "./Communities";
import Following from "./Following";
import Followers from "./Followers";
import Observed from "./Observed";
import { useGetPubliChatsQuery } from "../../../store/api/public-chat-api-slice";
import { PublicChatType } from "../../Chat/Chat";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useGetObservingQuery, useGetObserversQuery, useLazyGetUserDetailsQuery } from "../../../store/api/profile-api-slice";
import { toUserDetails } from "../../../store/api/result/dto/activity/UserRepresentation";
import { getDownloadURL, ref } from "firebase/storage";
import { UserProfileDTO } from "../../../store/api/result/dto/activity/UserProfileDTO";
import storage from "../../../config/firebase-config";
import { useKeycloak } from "@react-keycloak/web";

const transformPublicChatsToCommunityType = (publicChats: PublicChatType[] | undefined): Community[] | undefined => {
    if(!publicChats) return undefined;
    const communities: Community[] = [];
    publicChats.forEach(chat => communities.push({id: chat.id, name: chat.title}));
    return communities;
}

export interface CommunityPageProps {
};

const CommunityPage: React.FC<CommunityPageProps> = () => {
    const { username } = useParams();
    const [userDetails, setUserDetails] = useState<UserDetails>();
    const [userImages, setUserImages] = useState<Map<string, string>>(new Map());
    const [errorMessages, setErrorMessages] = useState<string[]>([]);
    const {data : publicChats} = useGetPubliChatsQuery();
    const communities = transformPublicChatsToCommunityType(publicChats);
    const [ getUserDetailsTrigger ] = useLazyGetUserDetailsQuery();
    const toast = useToast();
    const genericUserDetailsErrorMessage = "Could not fetch user details from our service";
    const { data: observingUsers } = useGetObserversQuery(username ?? "", {refetchOnMountOrArgChange: true });
    const { data: observedUsers } = useGetObservingQuery(username ?? "", {refetchOnMountOrArgChange: true});
    const { keycloak, initialized } = useKeycloak();

    useEffect(() => {

        if (username && initialized && keycloak.authenticated) {
            const fetchUserDetails = async () => {
                const response = await getUserDetailsTrigger(username);

                if (response.data) {
                    const details = toUserDetails(response.data);
                    setUserDetails(details);
                } else {
                    setErrorMessages([genericUserDetailsErrorMessage]);
                }
            }

            fetchUserDetails().catch(() => setErrorMessages([genericUserDetailsErrorMessage]));
        }
    }, [getUserDetailsTrigger, username, keycloak, initialized]);

    useEffect(() => {
        if (observedUsers && observingUsers && initialized && keycloak.authenticated) {
            const uniqueUsers = [...new Set([...observedUsers, ...observingUsers])];

            const fetchImages = async(uniqueUsers: UserProfileDTO[]) => {
                const userImages = new Map();
                Promise.all(uniqueUsers.map(async (user) => {
                    if(user.imageUrl !== "") {
                        const downloadUrl = await getDownloadURL(ref(storage, user.imageUrl));
                        userImages.set(user.username, downloadUrl);
                    }
                })).then(() => setUserImages(userImages));
            }

            fetchImages(uniqueUsers);
        }

    }, [observedUsers, observingUsers, keycloak, initialized])

    useEffect(() => {
        errorMessages.forEach(message => toast({
            title: "Something went wrong",
            description: message,
            status: "error",
            duration: 9000,
            isClosable: true,
        }))
    }, [errorMessages]);
    
    return <>
    {userDetails ?
    <VStack w="100%" spacing={0}>
        <ProfileBanner profileImage={meze} bannerImage={meze} userDetails={userDetails} observed={observedUsers ?? []} observers={observingUsers ?? []} />
        <Divider borderColor="purple.400" />
        <ProfileDescription description={userDetails ? userDetails.personal.description : ""} />
        <Divider borderColor="purple.400" />
        <TopPosts userDetails={userDetails} />
        <Divider borderColor="purple.400" />
        <Communities communities={communities ?? []} />
        <Divider borderColor="purple.400" />
        <Observed userDetails={userDetails} />
        <Divider borderColor="purple.400" />
        <Following users={observedUsers ?? []} usersImages={userImages} />
        <Divider borderColor="purple.400" />
        <Followers users={observingUsers ?? []} usersImages={userImages} />
    </VStack> : 
    <Center pt="16px">
        <Spinner color="primary.400" />
    </Center>}
    </>
}

export default CommunityPage;