import { ProductVariantOutputDTO } from "./ProductVariantOutputDTO";

export interface HeadphonesVariantOutputDTO extends ProductVariantOutputDTO {
    nominalImpedance: number,
    loudness: number,
    color: string,
    headphonesId: number
};

export function isHeadphonesVariantOutputDTO(variant: ProductVariantOutputDTO): variant is HeadphonesVariantOutputDTO {
    return "headphonesId" in variant;
}

export function isHeadphonesVariantOutputDTOArray(variants: ProductVariantOutputDTO[]): variants is HeadphonesVariantOutputDTO[] {
    return variants.every(isHeadphonesVariantOutputDTO);
}