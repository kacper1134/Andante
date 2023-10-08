export interface ProducerDTO {
    name: string,
    websiteUrl: string,
    imageUrl: string,
    productsIds: number[],
};

export function isProducerDTOArray(value: any[]): value is ProducerDTO[] {
    return value.every(isProducerDTO);
}

export function isProducerDTO(value: any): value is ProducerDTO {
    return "productsIds" in value;
}