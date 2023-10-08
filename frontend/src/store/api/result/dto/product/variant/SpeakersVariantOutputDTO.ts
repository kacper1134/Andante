import { ProductVariantOutputDTO } from "./ProductVariantOutputDTO";

export interface SpeakersVariantOutputDTO extends ProductVariantOutputDTO {
    loudness: number,
    color: string,
    speakersId: number
};

export function isSpeakersVariantOutputDTO(variant: ProductVariantOutputDTO): variant is SpeakersVariantOutputDTO {
    return "speakersId" in variant;
}

export function isSpeakersVariantOutputDTOArray(variants: ProductVariantOutputDTO[]): variants is SpeakersVariantOutputDTO[] {
    return variants.every(isSpeakersVariantOutputDTO);
}