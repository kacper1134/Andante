import { useKeycloak } from "@react-keycloak/web";
import Keycloak from "keycloak-js";
import {
  ChatObject,
  MultiChatSocket,
  MultiChatWindow,
  OptionsSettings,
  OptionsSettingsProps,
  useMultiChatLogic,
} from "react-chat-engine-advanced";
import { arrayEquals, dedupe } from "../../functions/array-functions";
import { getUserDetails } from "../../utils/KeycloakUtils";

import "./Chat.css";
import LeaveChatButton from "./LeaveChatButton";

export type ChatType = {
  title: string;
  is_direct_chat: boolean;
};

export type ChatUserType = {
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  secret: string;
};

export type PublicChatType = {
  id: number;
  title: string;
};

const getPersonalDetails = (keycloak: Keycloak) => {
  const idToken = keycloak.idTokenParsed;
  if (!idToken) return null;
  const details = getUserDetails(idToken);
  if (!details) return null;
  return details.personal;
};

const removeAdminFromPeople = (
  chat: ChatObject | undefined,
  username: string
) => {
  if (!chat) return undefined;
  const chatWithoutAdmin = { ...chat };
  chatWithoutAdmin.people = chat.people.filter(
    (person) =>
      person.person.username !== chat.admin.username ||
      chat.admin.username !== username
  );
  chatWithoutAdmin.people = chat.people.filter(
    (person) =>
      person.person.username !== "admin" && person.person.username !== "public"
  );
  return chatWithoutAdmin;
};

const Chat = () => {
  const { keycloak } = useKeycloak();
  const personal = getPersonalDetails(keycloak);
  const chatProps = useMultiChatLogic(
    process.env.REACT_APP_CHAT_ENGINE_ID as string,
    personal ? personal.username : "",
    personal ? personal.emailAddress : ""
  );

  const chats = chatProps.chats;
  if (!arrayEquals(chats, dedupe(chats))) {
    chatProps.setChats(dedupe(chats));
  }

  if (chatProps.activeChatId !== undefined && !chatProps.chat) {
    chatProps.setActiveChatId(undefined);
    chatProps.setMessages([]);
  }

  const isCurrentUserAdmin =
    chatProps.chat?.admin.username === (personal ? personal.username : "");

  const transformedChats = chatProps.chats.map(
    (chat) => removeAdminFromPeople(chat, personal ? personal.username : "")!
  );

  return (
    <>
      {personal && <MultiChatSocket {...chatProps} />}
      {personal && (
        <MultiChatWindow
          {...chatProps}
          chats={transformedChats}
          renderOptionsSettings={(props: OptionsSettingsProps) => {
            return isCurrentUserAdmin ? (
              <OptionsSettings {...chatProps} />
            ) : (
              <LeaveChatButton
                username={personal.username}
                activeChatId={chatProps.activeChatId!}
              />
            );
          }}
          chatSettingsColumnStyle={{
            display: chatProps.activeChatId ? "block" : "none",
          }}
          chatSettingsMobileButtonStyle={{
            display: chatProps.activeChatId ? "block" : "none",
          }}
        />
      )}
    </>
  );
};

export default Chat;
