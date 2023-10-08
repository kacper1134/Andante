import { createStandaloneToast } from "@chakra-ui/toast";
import { AnyAction } from "@reduxjs/toolkit";
import {
  BaseQueryFn,
  FetchArgs,
  FetchBaseQueryError,
  FetchBaseQueryMeta,
  MutationDefinition,
} from "@reduxjs/toolkit/dist/query";
import { MutationTrigger } from "@reduxjs/toolkit/dist/query/react/buildHooks";
import { DateTime } from "luxon";
import { Dispatch, SetStateAction } from "react";
import { OrderStatus } from "../../enums/OrderStatus";
import { add_purchase_interaction } from "../../functions/recommendation-functions";
import { InvoiceRequest } from "../../store/api/order-api-slice";
import { cartActions } from "../../store/cart/cartSlice";
import generateInvoice from "../Profile/History/InvoiceGenerator";
import { OrderData } from "./CartConfirmation";
import { CartEntryData } from "./CartEntry";
import { DeliveryPickupLocation } from "./CartForms";

const { toast } = createStandaloneToast();

export type OrderLocationType = {
  country: string;
  city: string;
  flatNumber: number | undefined;
  postCode: string;
  street: string;
  streetNumber: string;
  orderIds: number[];
  deliveryOrdersIds: number[];
};

export type OrderUserLocationType = {
  emailAddress: string;
  name: string;
  phoneNumber: string;
  surname: string;
  orderIds: number[];
};

export type OrderType = {
  deliveryCost: number;
  deliveryMethod: "Courier" | "PickupPoint";
  paymentMethod: "GooglePay" | "Visa" | "PayU";
  clientId: number;
  locationId: number;
  deliveryLocationId: number;
  status: "New";
  paymentCost: number;
  orderEntriesIds: OrderEntryType[];
};

export type OrderEntryType = {
  quantity: number;
  orderId: number;
  productVariantId: number;
};

type createLocationType = MutationTrigger<
  MutationDefinition<
    OrderLocationType,
    BaseQueryFn<
      string | FetchArgs,
      unknown,
      FetchBaseQueryError,
      {},
      FetchBaseQueryMeta
    >,
    never,
    number,
    "api"
  >
>;

type createUserType = MutationTrigger<
  MutationDefinition<
    OrderUserLocationType,
    BaseQueryFn<
      string | FetchArgs,
      unknown,
      FetchBaseQueryError,
      {},
      FetchBaseQueryMeta
    >,
    never,
    number,
    "api"
  >
>;

type createOrderType = MutationTrigger<
  MutationDefinition<
    OrderType,
    BaseQueryFn<
      string | FetchArgs,
      unknown,
      FetchBaseQueryError,
      {},
      FetchBaseQueryMeta
    >,
    never,
    number,
    "api"
  >
>;

type createOrderEntriesType = MutationTrigger<
  MutationDefinition<
    OrderEntryType[],
    BaseQueryFn<
      string | FetchArgs,
      unknown,
      FetchBaseQueryError,
      {},
      FetchBaseQueryMeta
    >,
    never,
    number[],
    "api"
  >
>;

type sendInvoiceType = MutationTrigger<
  MutationDefinition<
    InvoiceRequest,
    BaseQueryFn<
      string | FetchArgs,
      unknown,
      FetchBaseQueryError,
      {},
      FetchBaseQueryMeta
    >,
    never,
    void,
    "api"
  >
>;

type deleteOrderType = MutationTrigger<
  MutationDefinition<
    number,
    BaseQueryFn<
      string | FetchArgs,
      unknown,
      FetchBaseQueryError,
      {},
      FetchBaseQueryMeta
    >,
    "Order",
    void,
    "orderApi"
  >
>;

const startOrder = async (
  createLocation: createLocationType,
  createUser: createUserType,
  createOrder: createOrderType,
  createOrderEntries: createOrderEntriesType,
  sendInvoice: sendInvoiceType,
  deleteOrder: deleteOrderType,
  dispatch: Dispatch<AnyAction>,
  setIsSaving: Dispatch<SetStateAction<boolean>>,
  setIsModalOpen: Dispatch<SetStateAction<boolean>>,
  changeCurrentCartStep: (value: SetStateAction<number>) => void,
  orderData: OrderData,
  deliveryPickupLocation: DeliveryPickupLocation | null,
  cartItems: CartEntryData[],
  username: string
) => {
  let userLocationId = -1;
  let deliveryLocationId = -1;
  let userId = -1;
  let orderId = -1;
  setIsSaving(true);
  let locationResult = await createUserLocation(createLocation, orderData);
  if ("data" in locationResult) userLocationId = locationResult.data;

  locationResult = await createDeliveryLocation(
    createLocation,
    orderData,
    deliveryPickupLocation
  );
  if ("data" in locationResult) deliveryLocationId = locationResult.data;

  let userResult = await createOrderUser(createUser, orderData);
  if ("data" in userResult) userId = userResult.data;

  let orderResult = await createOrderFunction(
    createOrder,
    orderData,
    userLocationId,
    deliveryLocationId,
    userId
  );
  if ("data" in orderResult) orderId = orderResult.data;

  const orderEntries: OrderEntryType[] = cartItems.map((item) => {
    return {
      quantity: item.quantity,
      orderId,
      productVariantId: item.variant.id,
    };
  });

  const orderEntriesResult = await createOrderEntries(orderEntries);

  if ("data" in orderEntriesResult) {
    setIsSaving(false);
    changeCurrentCartStep(2);
    dispatch(
      cartActions.placeOrder({ orderDetails: orderData.orderDetails, orderId })
    );

    const invoice = generateInvoice(
      {
        orderId,
        orderedItems: cartItems,
        addressedDetails: {
          name: orderData.orderDetails.personal.name,
          surname: orderData.orderDetails.personal.surname,
          email: orderData.orderDetails.personal.emailAddress,
          phone: orderData.orderDetails.personal.phoneNumber,
        },
        orderDate: orderData.orderDate ?? DateTime.now(),
        shippingAddress: {
          country: orderData.orderDetails.location.country,
          city: orderData.orderDetails.location.city,
          street: orderData.orderDetails.location.street,
          postalCode: orderData.orderDetails.location.postalCode,
        },
        estimatedShipDate: orderData.orderDate ?? DateTime.now(),
        orderStatus: OrderStatus.NEW,
        orderCurrency: "$",
      },
      false
    )!;

    sendInvoice({ id: orderId, invoice });
    add_purchase_interaction(
      username,
      cartItems.map((item) => item.product?.id!)
    );
    dispatch(cartActions.clearCart());
    toast({
      title: "Place order",
      description: "Your order was successfully placed!",
      status: "success",
      isClosable: true,
    });
  } else {
    deleteOrder(orderId);
    setIsSaving(false);
    changeCurrentCartStep(0);
    let errorMessage = "";
    if ("data" in orderEntriesResult.error) {
      const errors: any = orderEntriesResult.error.data;
      errorMessage = errors.at(0);
    } else {
      errorMessage = "Unknown error";
    }

    toast({
      title: "Number of variant error",
      description: errorMessage,
      status: "error",
      isClosable: true,
      duration: 2000,
    });
  }

  setIsModalOpen(false);
};

const createUserLocation = (
  createLocation: createLocationType,
  orderData: OrderData
) => {
  const location = orderData.orderDetails.location;
  return createLocation({
    country: location.country,
    city: location.city,
    flatNumber: location.flatNumber ? +location.flatNumber! : undefined,
    postCode: location.postalCode,
    street: location.street,
    streetNumber: location.buildingNumber,
    orderIds: [],
    deliveryOrdersIds: [],
  });
};

const createDeliveryLocation = (
  createLocation: createLocationType,
  orderData: OrderData,
  deliveryPickupLocation: DeliveryPickupLocation | null
) => {
  if (orderData.deliveryMethod.type === "Courier") {
    return createUserLocation(createLocation, orderData);
  } else {
    const pickupLocation = deliveryPickupLocation!;

    return createLocation({
      country: pickupLocation.country,
      city: pickupLocation.city,
      postCode: pickupLocation.postCode,
      street: pickupLocation.street,
      flatNumber: undefined,
      streetNumber: pickupLocation.streetNumber,
      orderIds: [],
      deliveryOrdersIds: [],
    });
  }
};

const createOrderUser = (createUser: createUserType, orderData: OrderData) => {
  return createUser({
    emailAddress: orderData.orderDetails.personal.emailAddress,
    name: orderData.orderDetails.personal.name,
    surname: orderData.orderDetails.personal.surname,
    phoneNumber: orderData.orderDetails.personal.phoneNumber,
    orderIds: [],
  });
};

const createOrderFunction = (
  createOrder: createOrderType,
  orderData: OrderData,
  userLocationId: number,
  deliveryLocationId: number,
  userId: number
) => {
  const paymentMethod = orderData.paymentMethod.provider === "Online payment(PayU)" ? "PayU" : "Visa";

  return createOrder({
    deliveryCost: orderData.deliveryMethod.price,
    deliveryMethod:
      orderData.deliveryMethod.type === "Courier" ? "Courier" : "PickupPoint",
    paymentMethod: paymentMethod,
    clientId: userId,
    locationId: userLocationId,
    deliveryLocationId: deliveryLocationId,
    status: "New",
    paymentCost: orderData.paymentMethod.cost,
    orderEntriesIds: [],
  });
};

export default startOrder;
