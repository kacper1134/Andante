import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { RootState } from "..";
import { Page } from "./result/Page";
import { ProductOutputDTO, useGetByQuery } from "./result/dto/product/base/ProductOutputDTO";
import { CommentInputDTO } from "./result/dto/product/CommentInputDTO";
import { isProductOutputDTOArray } from "./result/dto/product/base/ProductOutputDTO";
import { CommentOutputDTO, isCommentOutputDTOPage } from "./result/dto/product/CommentOutputDTO";
import { ProducerDTO } from "./result/dto/product/ProducerDTO";
import { AmplifiersOutputDTO, useGetAmplifiersByQuery } from "./result/dto/product/base/AmplifiersOutputDTO";
import { GramophonesOutputDTO, useGetGramophonesByQuery } from "./result/dto/product/base/GramophonesOutputDTO";
import { HeadphonesOutputDTO, useGetHeadphonesByQuery } from "./result/dto/product/base/HeadphonesOutputDTO";
import { MicrophonesOutputDTO, useGetMicrophonesByQuery } from "./result/dto/product/base/MicrophonesOutputDTO";
import { SpeakersOutputDTO, useGetSpeakersByQuery } from "./result/dto/product/base/SpeakersOutputDTO";
import { SubwoofersOutputDTO, useGetSubwoofersByQuery } from "./result/dto/product/base/SubwoofersOutputDTO";
import { useSelector } from "react-redux";
import { buildQuery } from "../../utils/Utils";
import { ProductType } from "../../components/Shop/MainPage/FilterMenu/ProductCategory";
import { useMemo } from "react";
import { CommentStatistics } from "./result/dto/product/CommentStatistics";
import { NewsletterDTO } from "./result/dto/product/NewsletterDTO";

export enum ProductSortingOrder {
    PRICE_ASCENDING = "PRICE_ASCENDING",
    PRICE_DESCENDING = "PRICE_DESCENDING",
    RECENTLY_ADDED = "RECENTLY_ADDED",
    ALPHABETICAL = "ALPHABETICAL",
};

export enum CommentSortingOrder {
    NEWEST_FIRST = "NEWEST_FIRST",
    OLDEST_FIRST = "OLDEST_FIRST",
    HIGHEST_RATING = "HIGHEST_RATING",
    LOWEST_RATING = "LOWEST_RATING"
};

export interface ProductQuerySpecification {
    query: string,
    pageNumber: number,
    pageSize: number,
    sortingOrder: ProductSortingOrder,
}

export interface CommentQuerySpecification {
    query: string,
    pageNumber: number,
    pageSize: number,
    sortingOrder: CommentSortingOrder
};

const baseQuery = fetchBaseQuery({
    baseUrl: "http://localhost:4561",
    credentials: "include",
    prepareHeaders: (headers, { getState }) => {
        const token = (getState() as RootState).auth.idToken;
        
        if (token) {
            headers.set("Authorization", `Bearer ${token}`);
        }

        return headers;
    }
});

const productSlice = createApi({
    reducerPath: "shop",
    baseQuery: baseQuery,
    tagTypes: ["Product", "Observed", "Comments"],
    endpoints: (builder) => ({
        getByQuery: builder.query<Page<ProductOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
            transformResponse: (response: Page<ProductOutputDTO> | string[]) => response,
        }),
        getAmplifiersByQuery: builder.query<Page<AmplifiersOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/amplifier/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
        }),
        getGramophonesByQuery: builder.query<Page<GramophonesOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/gramophones/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
        }),
        getHeadphonesByQuery: builder.query<Page<HeadphonesOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/headphones/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
        }),
        getMicrophonesByQuery: builder.query<Page<MicrophonesOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/microphone/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
        }),
        getSpeakersByQuery: builder.query<Page<SpeakersOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/speakers/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
        }),
        getSubwoofersByQuery: builder.query<Page<SubwoofersOutputDTO> | string[], {query: ProductQuerySpecification, rating: number}>({
            query: ({query, rating}) => ({
                url: `/product/subwoofer/query?query=${query.query}&pageNumber=${query.pageNumber}&pageSize=${query.pageSize}&sortingOrder=${query.sortingOrder}&rating=${rating}`,
            }),
        }),
        getAllById: builder.query<ProductOutputDTO[] | string[], number[]>({
            query: (ids) => ({
                url: `/product/bulk?ids=${ids}`,
            }),
            transformResponse: (response: ProductOutputDTO[] | string[]) => response,
            providesTags: (result, error, arg) =>
                (result && isProductOutputDTOArray(result)) ? [...result.map(({ id }) => ({ type: "Product" as const, id})), "Product"]
                : ["Product"],
        }),
        getObserved: builder.query<ProductOutputDTO[] | string[], string>({
            query: (username) => ({
                url: `/product/user?username=${username}`,
            }),
            providesTags: (result, error, arg) => 
                (result && isProductOutputDTOArray(result)) ? [...result.map(({ id }) => ({ type: "Observed" as const, arg})), "Observed"]
                : ["Observed"],
        }),
        getBestRatedProducts: builder.query<ProductOutputDTO[] | string[], {page: number, size: number}>({
            query: ({page, size}) => ({
                url: `/product/rating?page=${page}&size=${size}`
            })
        }),
        getPopularProducts: builder.query<ProductOutputDTO[] | string[], {page: number, size: number}>({
            query: ({page, size}) => ({
                url: `/product/popular?page=${page}&size=${size}`
            })
        }),
        changeObservationStatus: builder.mutation<void, {productId: number, email: string}>({
            query: ({productId, email}) => ({
                url: `/product/status?id=${productId}&user=${email}`,
                method: "POST",
            }),
            invalidatesTags: (result, error, arg) => [{type: "Observed", id: arg.email}, {type: "Product", id: arg.productId}]
        }),
        getCommentsByQuery: builder.query<Page<CommentOutputDTO> | string[], CommentQuerySpecification>({
            query: ({query, pageNumber, pageSize, sortingOrder}) => ({
                url: `/product/comment/query?query=${query}&page=${pageNumber}&pageSize=${pageSize}&sortingOrder=${sortingOrder}`,
            }),
            providesTags: (result, error, arg) =>
            (result && isCommentOutputDTOPage(result)) ? [...result.content.map(({ id }) => ({ type: "Comments" as const, id})), "Comments"]
            : ["Comments"],
        }),
        getObservedComments: builder.query<CommentOutputDTO[] | string[], string>({
            query: (email) => ({
                url: `/product/comment/observed/${email}`,
            }),
        }),
        getTopCommentsByUser: builder.query<CommentOutputDTO[] | string[], {username: string, page: number, size: number}>({
            query: ({username, page, size}) => ({
                url: `/product/comment/top?username=${username}&page=${page}&size=${size}`,
            })
        }),
        getCommentStatistics: builder.query<CommentStatistics, string>({
            query: (username) => ({
                url: `/product/comment/statistics/${username}`,
            }),
        }),
        createComment: builder.mutation<void, CommentInputDTO>({
            query: (comment) => ({
                url: "/product/comment",
                method: "POST",
                body: comment,
            }),
            invalidatesTags: (result, error, arg) => [{ type: "Product", id: arg.productId}]
        }),
        editComment: builder.mutation<void, CommentInputDTO>({
            query: (comment) => ({
                url: "/product/comment",
                method: "PUT",
                body: comment,
            }),
            invalidatesTags: (result, error, arg) => [{ type: "Product", id: arg.productId}]
        }),
        changeCommentObservationStatus: builder.mutation<void, {email: string, id: number}>({
            query: ({email, id}) => ({
                url: `/product/comment/favourite?user=${email}&id=${id}`,
                method: "POST",
            }),
            invalidatesTags: (result, error, arg) => [{ type: "Comments", id: arg.id}],
        }),
        subscribeToNewsletter: builder.mutation<NewsletterDTO, string>({
            query: (email) => ({
                url: `/activity/newsletter/subscribe?email=${email}`,
                method: "POST",
            }),
        }),
        getTopProducers: builder.query<ProducerDTO[] | string[], {page: number, size: number}>({
            query: ({page, size}) => ({
                url: `/product/producer/top?page=${page}&size=${size}`,
            }),
        }),
    }),
});

export const { useLazyGetByQueryQuery, useLazyGetAmplifiersByQueryQuery, useLazyGetGramophonesByQueryQuery, useLazyGetHeadphonesByQueryQuery, 
    useLazyGetMicrophonesByQueryQuery, useLazyGetSpeakersByQueryQuery, useLazyGetSubwoofersByQueryQuery, useLazyGetBestRatedProductsQuery, 
    useLazyGetTopProducersQuery, useGetAllByIdQuery, useGetObservedQuery, useGetCommentsByQueryQuery, useGetTopCommentsByUserQuery, useChangeObservationStatusMutation, 
    useSubscribeToNewsletterMutation, useChangeCommentObservationStatusMutation, useCreateCommentMutation, useGetObservedCommentsQuery, useGetCommentStatisticsQuery, useGetPopularProductsQuery, useLazyGetAllByIdQuery, useEditCommentMutation } = productSlice;

export function useGetByFilterState(page: number, pageSize: number, sortingOrder: ProductSortingOrder): Page<ProductOutputDTO> {
    const filterState = useSelector((state: RootState) => state.inner.filterState);
    const query = buildQuery(filterState.query, filterState.selectedCategory, filterState.priceRange, filterState.selectedStates);
    const querySpecification: ProductQuerySpecification = useMemo(() => {
    return {
        query: query,
        pageNumber: page,
        pageSize: pageSize,
        sortingOrder: sortingOrder,
    }}, [query, page, pageSize, sortingOrder]);

    let hookToCall: (querySpecification: ProductQuerySpecification, minimumRating: number) => Page<ProductOutputDTO>;

    if (!filterState.selectedCategory) {
        hookToCall = useGetByQuery;
    } else {
        switch (filterState.selectedCategory.category) {
            case ProductType.AMPLIFIERS:
                hookToCall = useGetAmplifiersByQuery;
                break;
            case ProductType.GRAMOPHONES:
                hookToCall = useGetGramophonesByQuery;
                break;
            case ProductType.HEADPHONES:
                hookToCall = useGetHeadphonesByQuery;
                break;
            case ProductType.MICROPHONES:
                hookToCall = useGetMicrophonesByQuery;
                break;
            case ProductType.SPEAKERS:
                hookToCall = useGetSpeakersByQuery;
                break;
            case ProductType.SUBWOOFERS:
                hookToCall = useGetSubwoofersByQuery;   
        }
    }

    const result = hookToCall(querySpecification, filterState.minimumRating);

    return result;
}
    

export default productSlice;