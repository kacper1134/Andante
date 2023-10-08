import { Flex } from "@chakra-ui/react";
import Chat from "../components/Chat/Chat";
import useAuthentication from "../hooks/useAuthentication";

const ChatPage = () => {
  useAuthentication("/chat");
  return (
    <Flex direction="column" h="100%" >
      <Chat />
    </Flex>
  );
};

export default ChatPage;
