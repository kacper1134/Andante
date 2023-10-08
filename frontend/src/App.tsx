import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { Flex, useBreakpointValue, Spacer } from "@chakra-ui/react";
import Navbar from "./components/Navbar/Navbar";
import HomePage from "./pages/HomePage";
import ProfilePage from "./pages/ProfilePage";
import FilteredPage from "./pages/FilteredPage";
import ProfileNavigation from "./components/Profile/Navigation/ProfileNavigation";
import { AnimatePresence } from "framer-motion";
import Footer from "./components/Footer/Footer";
import ShopMainPage from "./pages/ShopMainPage";
import CartPage from "./pages/CartPage";
import ForumHomePage from "./pages/ForumHomePage";
import About from "./pages/About";
import ChatPage from "./pages/ChatPage";
import BlogMainPage from "./pages/BlogMainPage";
import NotificationsPage from "./pages/NotificationsPage";

const App = () => {
  const location = useLocation();
  const isLarge = useBreakpointValue({ base: false, lg: true })!;
  const menuHeight = useBreakpointValue({
    base: "24px",
    sm: "32px",
    md: "40px",
    lg: "56px",
    xl: "64px",
    "2xl": "72px",
  })!;

  return (
    <Flex>
      <AnimatePresence>
        {location.pathname.startsWith("/profile") && isLarge && (
          <ProfileNavigation
            navbarHeight={menuHeight}
            isSidebarOpened={isLarge}
          />
        )}
      </AnimatePresence>
      <Flex
        flexDirection="column"
        minHeight="100vh"
        width={0}
        flexGrow={1}
        flexShrink={1}
      >
        <Navbar height={menuHeight} />
        <Routes>
          <Route path="home" element={<HomePage />} />
          <Route
            path="/shop/filtered"
            element={<FilteredPage menuHeight={menuHeight} />}
          />
          <Route path="/music/*" element={<BlogMainPage topic="music" />} />
          <Route path="/reviews/*" element={<BlogMainPage topic="reviews" />} />
          <Route path="/recommended/*" element={<BlogMainPage topic="recommended" />} />
          <Route path="/shop/*" element={<ShopMainPage />} />
          <Route path="/forum/*" element={<ForumHomePage />} />
          <Route
            path="profile/*"
            element={<ProfilePage isSidebarOpened={isLarge} />}
          />
          <Route path="notifications/*" element={<NotificationsPage />} />
          <Route path="/about/*" element={<About />} />
          <Route path="/cart/*" element={<CartPage />} />
          <Route path="/chat" element={<ChatPage />} />
          <Route path="/" element={<Navigate to="/home" />} />
        </Routes>
        {location.pathname !== "/chat" && (
          <>
            <Spacer />
            <Footer />
          </>
        )}
      </Flex>
    </Flex>
  );
};

export default App;
