import { ProductVariantOutputDTO } from "./ProductVariantOutputDTO";

export interface AmplifiersVariantOutputDTO extends ProductVariantOutputDTO {
    color: string,
    amplifiersId: number
};

export function isAmplifiersVariant(variant: ProductVariantOutputDTO): variant is AmplifiersVariantOutputDTO {
    return "amplifiersId" in variant;
}

export function isAmplifiersVariantArray(variants: ProductVariantOutputDTO[]): variants is AmplifiersVariantOutputDTO[] {
    return variants.every(isAmplifiersVariant);
}