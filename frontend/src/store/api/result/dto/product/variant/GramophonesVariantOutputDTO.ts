import { ProductVariantOutputDTO } from "./ProductVariantOutputDTO";

export interface GramophonesVariantOutputDTO extends ProductVariantOutputDTO {
    color: string,
    gramophonesId: number
};

export function isGramophonesVariantOutputDTO(variant: ProductVariantOutputDTO): variant is GramophonesVariantOutputDTO {
    return "gramophonesId" in variant;
}

export function isGramophonesVariantOutputDTOArray(variants: ProductVariantOutputDTO[]): variants is GramophonesVariantOutputDTO[] {
    return variants.every(isGramophonesVariantOutputDTO);
}