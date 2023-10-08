import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { RootState } from "..";
import { PostInputDTO } from "./result/dto/forum/PostInputDTO";
import { PostOutputDTO } from "./result/dto/forum/PostOutputDTO";
import { PostResponseInputDTO } from "./result/dto/forum/PostResponseInputDTO";
import { PostResponseLikeDTO } from "./result/dto/forum/PostResponseLikeDTO";
import { PostResponseOutputDTO } from "./result/dto/forum/PostResponseOutputDTO";
import { TopicOutputDTO } from "./result/dto/forum/TopicOutputDTO";
import { UserDTO } from "./result/dto/forum/UserDTO";
import { Page } from "./result/Page";

export enum TopicSortingOrder {
  NEWEST_FIRST = "NEWEST_FIRST",
  OLDEST_FIRST = "OLDEST_FIRST",
  ALPHABETICAL = "ALPHABETICAL",
  REVERSE_ALPHABETICAL = "REVERSE_ALPHABETICAL",
}

export interface TopicQuerySpecification {
  query: string;
  pageNumber: number;
  pageAmount: number;
  sortingOrder: TopicSortingOrder;
}

export interface PostQuerySpecification {
  query: string;
  pageNumber: number;
  pageSize: number;
  sortingOrder: TopicSortingOrder;
}

const baseQuery = fetchBaseQuery({
  baseUrl: "http://localhost:4561",
  credentials: "include",
  prepareHeaders: (headers, { getState }) => {
    const token = (getState() as RootState).auth.idToken;

    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    return headers;
  },
});

const forumSlice = createApi({
  reducerPath: "forum",
  baseQuery: baseQuery,
  tagTypes: ["Posts", "Responses"],
  endpoints: (builder) => ({
    getTopicByQuery: builder.query<
      Page<TopicOutputDTO> | string[],
      { query: TopicQuerySpecification }
    >({
      query: ({ query }) => ({
        url: `/forum/topic/page?query=${query.query}&pageNumber=${query.pageNumber}&pageAmount=${query.pageAmount}&sortingOrder=${query.sortingOrder}`,
      }),
      transformResponse: (response: Page<TopicOutputDTO> | string[]) =>
        response,
    }),
    getPostByQuery: builder.query<
      Page<PostOutputDTO> | string[],
      { query: PostQuerySpecification }
    >({
      query: ({ query }) => ({
        url: `/forum/post/page?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}`,
      }),
      transformResponse: (response: Page<PostOutputDTO> | string[]) => response,
      providesTags: (result, error, arg) =>
        result && "content" in result
          ? [
              ...result.content.map(({ id }) => ({
                type: "Posts" as const,
                id,
              })),
              "Posts",
            ]
          : ["Posts"],
    }),
    getPostResponseByQuery: builder.query<
      Page<PostResponseOutputDTO> | string[],
      { query: PostQuerySpecification }
    >({
      query: ({ query }) => ({
        url: `/forum/response/page?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}`,
      }),
      transformResponse: (response: Page<PostResponseOutputDTO> | string[]) =>
        response,
      providesTags: (result, error, arg) =>
        result && "content" in result
          ? [
              ...result.content.map(({ id }) => ({
                type: "Responses" as const,
                id,
              })),
              "Responses",
            ]
          : ["Responses"],
    }),
    getLikedPosts: builder.query<PostOutputDTO[], string>({
      query: (email) => ({
        url: `/forum/post/${email}/liked`,
      }),
    }),
    getSubtopics: builder.query<
      TopicOutputDTO[] | string[],
      { topicId: number }
    >({
      query: ({ topicId }) => ({
        url: `/forum/topic/subtopics?id=${topicId}`,
      }),
      transformResponse: (response: TopicOutputDTO[] | string[]) => response,
    }),
    getTopicsHierarchy: builder.query<
      TopicOutputDTO[] | string[],
      { topicId: number }
    >({
      query: ({ topicId }) => ({
        url: `/forum/topic/hierarchy/${topicId}`,
      }),
      transformResponse: (response: TopicOutputDTO[] | string[]) => response,
    }),
    getTopic: builder.query<TopicOutputDTO | string, { topicId: number }>({
      query: ({ topicId }) => ({
        url: `/forum/topic?id=${topicId}`,
      }),
      transformResponse: (response: TopicOutputDTO | string) => response,
    }),
    createUser: builder.mutation<string, UserDTO>({
      query: (body) => ({
        url: "/forum/user",
        method: "POST",
        body,
        responseHandler: "text",
      }),
      transformResponse: (response: string) => response,
    }),
    createPost: builder.mutation<number, PostInputDTO>({
      query: (body) => ({
        url: "/forum/post",
        method: "POST",
        body,
      }),
      transformResponse: (response: number) => response,
      invalidatesTags: (result, error, arg) => [
        { type: "Posts", id: arg.topicId },
      ],
    }),
    deletePost: builder.mutation<void, {topicId: number, postId: number}>({
      query: ({topicId, postId}) => ({
        url: `/forum/post/${postId}`,
        method: "DELETE",
      }),
      invalidatesTags: (result, error, arg) => [
        { type: "Posts", id: arg.topicId },
      ],
    }),
    getPost: builder.query<PostOutputDTO | string, { postId: number }>({
      query: ({ postId }) => ({
        url: `/forum/post?id=${postId}`,
      }),
      transformResponse: (response: PostOutputDTO | string) => response,
    }),
    createPostResponse: builder.mutation<number, PostResponseInputDTO>({
      query: (body) => ({
        url: "/forum/response",
        method: "POST",
        body,
      }),
      transformResponse: (response: number) => response,
      invalidatesTags: (result, error, arg) => [
        { type: "Responses", id: arg.postId },
      ],
    }),
    updatePost: builder.mutation<string, PostInputDTO>({
      query: (body) => ({
        url: "/forum/post",
        method: "PUT",
        body,
      }),
      transformResponse: (response: string) => response,
      invalidatesTags: (result, error, arg) => [
        { type: "Posts", id: arg.topicId },
      ],
    }),
    updatePostReply: builder.mutation<string, PostResponseInputDTO>({
      query: (body) => ({
        url: "/forum/response",
        method: "PUT",
        body,
      }),
      transformResponse: (response: string) => response,
      invalidatesTags: (result, error, arg) => [
        { type: "Responses", id: arg.postId },
      ],
    }),
    likePost: builder.mutation<PostOutputDTO, {body: PostResponseLikeDTO, topicId: number}>({
      query: ({body, topicId}) => ({
        url: "/forum/post/like",
        method: "POST",
        body,
      }),
      invalidatesTags: (result, error, arg) => [
        { type: "Posts", id: arg.topicId },
      ],
    }),
    likeResponse: builder.mutation<PostResponseOutputDTO, {body: PostResponseLikeDTO, postId: number}>({
      query: ({body, postId}) => ({
        url: "/forum/response/like",
        method: "POST",
        body,
      }),
      invalidatesTags: (result, error, arg) => [
        { type: "Responses", id: arg.postId },
      ],
    }),
    getTopTopics: builder.query<
      Page<TopicOutputDTO> | string,
      { page: number; count: number }
    >({
      query: ({ page, count }) => ({
        url: `/forum/topic/top?page=${page}&count=${count}`,
      }),
      transformResponse: (response: Page<TopicOutputDTO> | string) => response,
    }),
  }),
});

export const {
  useLazyGetTopicByQueryQuery,
  useGetLikedPostsQuery,
  useLazyGetSubtopicsQuery,
  useLazyGetTopicQuery,
  useLazyGetPostByQueryQuery,
  useCreateUserMutation,
  useCreatePostMutation,
  useLazyGetPostQuery,
  useLazyGetPostResponseByQueryQuery,
  useCreatePostResponseMutation,
  useUpdatePostMutation,
  useUpdatePostReplyMutation,
  useLikeResponseMutation,
  useLikePostMutation,
  useLazyGetTopTopicsQuery,
  useLazyGetTopicsHierarchyQuery,
  useDeletePostMutation,
} = forumSlice;

export default forumSlice;
