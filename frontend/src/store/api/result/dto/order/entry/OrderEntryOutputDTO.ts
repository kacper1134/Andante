import { ProductVariantOutputDTO } from "../../product/variant/ProductVariantOutputDTO";
import { OrderOutputDTO } from "../base/OrderOutputDTO";

export interface OrderEntryOutputDTO {
    id: number,
    quantity: number,
    order: OrderOutputDTO,
    productVariant: ProductVariantOutputDTO,
};