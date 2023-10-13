import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import {
  OrderEntryType,
  OrderLocationType,
  OrderType,
  OrderUserLocationType,
} from "../../components/Cart/startOrder";
import { OrderResultType } from "../../components/Profile/History/OrderHistory";
import { UpdateOrderType } from "../../components/Profile/History/OrdersList";
import { OrderStatus } from "../../enums/OrderStatus";
import { RootState } from "../index";
import { OrderEntryOutputDTO } from "./result/dto/order/entry/OrderEntryOutputDTO";

export interface InvoiceRequest {
  id: number;
  invoice: Blob;
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

const orderApiSlice = createApi({
  reducerPath: "orderApi",
  baseQuery: baseQuery,
  tagTypes: ["Order"],
  endpoints: (builder) => ({
    startPayment: builder.mutation<string, number>({
      query: (orderId) => ({
        url: `/order/payment?orderId=${orderId}`,
        method: "POST",
        responseHandler: (response) => response.text(),
      }),
    }),
    createOrderLocation: builder.mutation<number, OrderLocationType>({
      query: (location) => ({
        url: "/order/location",
        method: "POST",
        body: location,
      }),
    }),
    createOrderUser: builder.mutation<number, OrderUserLocationType>({
      query: (user) => ({
        url: "/order/user",
        method: "POST",
        body: user,
      }),
    }),
    createOrder: builder.mutation<number, OrderType>({
      query: (order) => ({
        url: "/order/create",
        method: "POST",
        body: order,
      }),
      invalidatesTags: ["Order"],
    }),
    deleteOrder: builder.mutation<void, number>({
      query: (orderId) => ({
        url: `/order/${orderId}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Order"],
    }),
    createOrderEntries: builder.mutation<number[], OrderEntryType[]>({
      query: (orderEntries) => ({
        url: "/order/orderEntry/bulk",
        method: "POST",
        body: orderEntries,
      }),
    }),
    sendInvoice: builder.mutation<void, InvoiceRequest>({
      query: (invoiceRequest) => ({
        url: `/order/invoice/${invoiceRequest.id}`,
        method: "POST",
        body: invoiceRequest.invoice,
      }),
    }),

    getOrders: builder.query<
      OrderResultType,
      {
        email: string;
        status: OrderStatus;
        page: number;
        count: number;
        sorting_order: "NEWEST_FIRST" | "OLDEST_FIRST";
      }
    >({
      query: ({ email, status, page, count, sorting_order }) => ({
        url: `/order/client/${email}?status=${status}&page=${page}&count=${count}&order=${sorting_order}`,
      }),
      providesTags: ["Order"],
    }),
    getOrderEntries: builder.query<OrderEntryOutputDTO[], number>({
      query: (orderId) => ({
        url: `/order/orderEntry/bulk/order/` + orderId,
      }),
    }),
    getOrder: builder.query<OrderResultType, number>({
      query: (orderId) => ({
        url: `/order/` + orderId,
      }),
      providesTags: ["Order"],
    }),
    updateOrder: builder.mutation<void, UpdateOrderType>({
      query: (body) => ({
        url: `/order`,
        method: "PUT",
        body,
      }),
      invalidatesTags: ["Order"],
    }),
  }),
});

export const {
  useStartPaymentMutation,
  useCreateOrderLocationMutation,
  useSendInvoiceMutation,
  useCreateOrderUserMutation,
  useCreateOrderMutation,
  useCreateOrderEntriesMutation,
  useLazyGetOrdersQuery,
  useLazyGetOrderEntriesQuery,
  useGetOrderQuery,
  useLazyGetOrderQuery,
  useUpdateOrderMutation,
  useDeleteOrderMutation,
} = orderApiSlice;
export default orderApiSlice;
