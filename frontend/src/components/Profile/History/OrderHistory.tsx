import { Spacer, VStack, Spinner, Center } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { OrderDetails, OrdersList } from "./OrdersList";
import { OrderStatus } from "../../../enums/OrderStatus";
import PageSelector from "../../Shop/ResultsPage/PageSelector";
import { useKeycloak } from "@react-keycloak/web";
import {
  useLazyGetOrderEntriesQuery,
  useLazyGetOrdersQuery,
} from "../../../store/api/order-api-slice";
import { DateTime } from "luxon";
import { isAmplifiersVariant } from "../../../store/api/result/dto/product/variant/AmplifiersVariantOutputDTO";
import { isGramophonesVariantOutputDTO } from "../../../store/api/result/dto/product/variant/GramophonesVariantOutputDTO";
import { isHeadphonesVariantOutputDTO } from "../../../store/api/result/dto/product/variant/HeadphonesVariantOutputDTO";
import { isMicrophoneVariantOutputDTO } from "../../../store/api/result/dto/product/variant/MicrophonesVariantOutputDTO";
import { isSpeakersVariantOutputDTO } from "../../../store/api/result/dto/product/variant/SpeakersVariantOutputDTO";
import { isSubwoofersVariantOutputDTO } from "../../../store/api/result/dto/product/variant/SubwoofersVariantOutputDTO";
import { RootState } from "../../../store";
import { useSelector } from "react-redux";

export interface OrderHistoryProps {
  orderStatus: OrderStatus | string;
  reloadCanceled: boolean;
  setReloadCanceled: React.Dispatch<React.SetStateAction<boolean>>;
  sortingOrder: SortingOrder;
}

export enum SortingOrder {
  TIME_ASCENDING = "Oldest: First",
  TIME_DESCENDING = "Newest: First",
}

type LocationType = {
  id: number;
  city: string;
  country: string;
  postCode: string;
  street: string;
  streetNumber: string;
};

export type OrderResultType = {
  content: {
    id: number;
    creationTimestamp: string;
    deliveryMethod: string;
    paymentMethod: string;
    totalCost: number;
    location: LocationType;
    deliveryLocation: LocationType;
    deliveryCost: number;
    paymentCost: number;
    client: {
      id: number;
      emailAddress: string;
      name: string;
      surname: string;
      phoneNumber: string;
    };
  }[];
  deliveryMethod: "Courier" | "Pickup Point";
  totalPages: number;
};

export type OrderEntriesType = {
  id: number;
  productVariant: {
    id: number;
    imageUrl: string;
    price: number;
    productName: string;
  };
  quantity: number;
};

const OrderHistory: React.FC<OrderHistoryProps> = ({
  orderStatus,
  reloadCanceled,
  setReloadCanceled,
  sortingOrder,
}) => {
  const [getOrder] = useLazyGetOrdersQuery();
  const [getOrderEntries] = useLazyGetOrderEntriesQuery();
  const [reload, setReload] = useState(false);
  const [loading, setLoading] = useState(true);

  const { keycloak, initialized } = useKeycloak();
  const [currentPage, setCurrentPage] = useState(0);
  const [totalNumberOfPages, setTotalNumberOfPages] = useState(0);
  const [orders, setOrders] = useState<OrderDetails[]>();
  const userDetails = useSelector((state: RootState) => state.auth.userDetails);
  
  useEffect(() => {
    const getEntries = async (orders: OrderDetails[]) => {
      for (let i = 0; i < orders.length; i++) {
        const entries = await getOrderEntries(orders[i].orderId);

        if (entries !== undefined && entries.data!.length > 0) {
          const headerEntry = entries.data?.at(0);
          orders.at(i)!.orderImage = headerEntry?.productVariant.imageUrl!;
          orders.at(i)!.orderTitle = headerEntry?.productVariant.productName!;
        }
        orders.at(i)!.orderedItems = entries.data
          ? entries.data.map((entry) => {
              let productId = -1;
              const productVariant = entry.productVariant;
              if (isAmplifiersVariant(productVariant)) {
                productId = productVariant.amplifiersId;
              } else if (isGramophonesVariantOutputDTO(productVariant)) {
                productId = productVariant.gramophonesId;
              } else if (isHeadphonesVariantOutputDTO(productVariant)) {
                productId = productVariant.headphonesId;
              } else if (isMicrophoneVariantOutputDTO(productVariant)) {
                productId = productVariant.microphoneId;
              } else if (isSpeakersVariantOutputDTO(productVariant)) {
                productId = productVariant.speakersId;
              } else if (isSubwoofersVariantOutputDTO(productVariant)) {
                productId = productVariant.subwoofersId;
              }
              return {
                productId: productId,
                variant: productVariant,
                quantity: entry.quantity,
              };
            })
          : [];
      }
    };

    if (
      (initialized && keycloak.authenticated) ||
      reload ||
      (reloadCanceled && orderStatus === OrderStatus.CANCELED)
    ) {
      setLoading(true);
      getOrder({
        email: userDetails?.personal.emailAddress!,
        status: (orderStatus as OrderStatus),
        page: currentPage,
        count: 3,
        sorting_order:
          sortingOrder === SortingOrder.TIME_DESCENDING
            ? "NEWEST_FIRST"
            : "OLDEST_FIRST",
      })
        .then(async (result) => {
          setTotalNumberOfPages(result.data?.totalPages!);
          let orders: OrderDetails[] = [];

          result.data?.content.forEach((order) => {
            const date = new Date(order.creationTimestamp);
            const deliveryDate = new Date(date);

            deliveryDate.setDate(
              deliveryDate.getDate() +
                (order.deliveryMethod === "PickupPoint" ? 2 : 1)
            );

            orders.push({
              orderId: order.id,
              orderImage: "",
              orderTitle: "",
              paymentMethod: order.paymentMethod,
              deliveryMethod: order.deliveryMethod,
              deliveryCost: order.deliveryCost,
              deliveryLocationId: order.deliveryLocation.id,
              locationId: order.location.id,
              paymentCost: order.paymentCost,
              orderedItems: [],
              addressedDetails: {
                id: order.client.id,
                name: order.client.name,
                surname: order.client.surname,
                email: order.client.emailAddress,
                phone: order.client.phoneNumber,
              },
              orderDate: DateTime.fromJSDate(date),
              shippingAddress: {
                country: order.deliveryLocation.country,
                city: order.deliveryLocation.city,
                street: order.deliveryLocation.street,
                postalCode: order.deliveryLocation.postCode,
              },
              estimatedShipDate: DateTime.fromJSDate(deliveryDate),
              orderStatus: (OrderStatus as any)[orderStatus.toString()],
              totalCost: order.totalCost,
              orderCurrency: "$",
            });
          });

          await getEntries(orders);

          setOrders(orders);
          setReload(false);
          setLoading(false);
          if (reload) setReloadCanceled(true);
          if (reloadCanceled) setReloadCanceled(false);
        })
        .catch(() => setLoading(false));
    }
  }, [
    currentPage,
    initialized,
    keycloak.authenticated,
    orderStatus,
    sortingOrder,
    getOrder,
    getOrderEntries,
    setReload,
    reload,
    setReloadCanceled,
    reloadCanceled,
  ]);

  return (
    <VStack h="inherit">
      {loading && (
        <Center h="100%">
          <Spinner size="lg" color="primary.700" />
        </Center>
      )}

      <>
        {!loading && <OrdersList orders={orders ?? []} setReload={setReload} />}
        <Spacer />
        {totalNumberOfPages !== 0 && (
          <PageSelector
            currentPage={currentPage}
            setCurrentPage={setCurrentPage}
            totalNumberOfPages={totalNumberOfPages}
          />
        )}
      </>
    </VStack>
  );
};

export default OrderHistory;
