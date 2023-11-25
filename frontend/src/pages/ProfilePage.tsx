import { Routes, Route, Navigate } from "react-router-dom";
import ProfileDetails from "../components/Profile/Details/ProfileDetails";
import ProfileLinks from "../components/Profile/Navigation/ProfileLink/ProfileLinks";
import ProfileHistory from "../components/Profile/History/ProfileHistory";
import CommunityPage from "../components/Profile/Community/CommunityPage";
import useAuthentication from "../hooks/useAuthentication";
import ProfileEdit from "../components/Profile/Edit/ProfileEdit";
import OrderPreferencesForms from "../components/Profile/Preferences/OrderPreferencesForms";

export interface ProfilePageProps {
    isSidebarOpened: boolean,
};

const ProfilePage: React.FC<ProfilePageProps> = ({isSidebarOpened}) => {
    useAuthentication("/profile");

    return (
        <>
        {!isSidebarOpened && <ProfileLinks isSidebarOpened={false} />}
        <Routes>
            <Route path="details" element={<ProfileDetails />} />
            <Route path="orders" element={<ProfileHistory />} />
            <Route path="community/:username" element={<CommunityPage />} />
            <Route path="edit" element={<ProfileEdit />} />
            <Route path="preferences" element={<OrderPreferencesForms />} />
            <Route path="/" element={<Navigate to="details" />} />
        </Routes>
        </>
    )
};

export default ProfilePage;