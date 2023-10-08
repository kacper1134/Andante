import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { ChatUserType } from "../../components/Chat/Chat";
import { getUserDetails } from "../../utils/KeycloakUtils";
import { RootState } from "../index";

const chatQuery = fetchBaseQuery({
  baseUrl: "https://api.chatengine.io/",
  prepareHeaders(headers, { getState }) {
    const token = (getState() as RootState).auth.tokenParsed;
    if (token) {
      const details = getUserDetails(token);
      headers.set("Project-ID", process.env.REACT_APP_CHAT_ENGINE_ID as string);
      headers.set(
        "PRIVATE-KEY",
        process.env.REACT_APP_CHAT_ENGINE_KEY as string
      );
      headers.set("User-Name", details.personal.username);
      headers.set("User-Secret", details.personal.emailAddress);
    }
    return headers;
  },
});

const chatApiSlice = createApi({
  reducerPath: "chatApi",
  baseQuery: chatQuery,
  tagTypes: [],
  endpoints: (builder) => ({
    createUser: builder.mutation<any, ChatUserType>({
      query: (body) => ({
        url: "users/",
        method: "PUT",
        body,
      }),
    }),
    leaveChat: builder.mutation<
      any,
      { body: { username: string }; chatId: number }
    >({
      query: (input) => ({
        url: `chats/${input.chatId}/people/`,
        method: "DELETE",
        body: input.body,
      }),
    }),
  }),
});

export const { useCreateUserMutation, useLeaveChatMutation } = chatApiSlice;
export default chatApiSlice;
