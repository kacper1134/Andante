import { ClientDTO } from "../client/ClientDTO";
import { LocationDTO } from "../location/LocationDTO";


export interface OrderOutputDTO {
    id: number,
    creationTimestamp: string,
    deliveryCost: number,
    deliveryMethod: string,
    paymentMethod: string,
    client: ClientDTO,
    location: LocationDTO,
    deliveryLocation: LocationDTO,
    status: string,
    paymentCost: number,
    totalCost: number,
    orderEntriesIds: number[],
}