export interface ProductVariantOutputDTO {
    id: number,
    price: number,
    availableQuantity: number,
    imageUrl: string,
    thumbnailUrl: string,
    observers: string[],
    productName: string,
}