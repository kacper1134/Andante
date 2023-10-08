import { ProductVariantOutputDTO } from "./ProductVariantOutputDTO";

export interface SubwoofersVariantOutputDTO extends ProductVariantOutputDTO {
    color: string,
    subwoofersId: number,
};

export function isSubwoofersVariantOutputDTO(variant: ProductVariantOutputDTO): variant is SubwoofersVariantOutputDTO {
    return "subwoofersId" in variant;
}

export function isSubwoofersVariantOutputDTOArray(variants: ProductVariantOutputDTO[]): variants is SubwoofersVariantOutputDTO[] {
    return variants.every(isSubwoofersVariantOutputDTO);
}