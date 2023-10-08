import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const chatQuery = fetchBaseQuery({
  baseUrl: "https://api.chatengine.io/",
  prepareHeaders(headers, { getState }) {
    headers.set("Project-ID", process.env.REACT_APP_CHAT_ENGINE_ID as string);
    headers.set("PRIVATE-KEY", process.env.REACT_APP_CHAT_ENGINE_KEY as string);
    headers.set(
      "User-Name",
      process.env.REACT_APP_PUBLIC_CHAT_USERNAME as string
    );
    headers.set(
      "User-Secret",
      process.env.REACT_APP_PUBLIC_CHAT_PASSWORD as string
    );
    return headers;
  },
});

const publicChatApiSlice = createApi({
  reducerPath: "publicChatApi",
  baseQuery: chatQuery,
  tagTypes: [],
  endpoints: (builder) => ({
    getPubliChats: builder.query<any[], void>({
      query: () => ({
        url: "chats/",
      }),
    }),
    joinChat: builder.mutation<
      any,
      { chatId: number; body: { username: string } }
    >({
      query: (input) => ({
        url: `chats/${input.chatId}/people/`,
        method: "POST",
        body: input.body,
      }),
    }),
  }),
});

export const { useGetPubliChatsQuery, useJoinChatMutation } = publicChatApiSlice;
export default publicChatApiSlice;
