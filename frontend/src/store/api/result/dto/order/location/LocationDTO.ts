export interface LocationDTO {
    id: number,
    city: string,
    country: string,
    flatNumber: number,
    postCode: string,
    street: string,
    streetNumber: number,
    orderIds: number[],
    deliveryOrdersIds: number[]
};