import { ProductVariantOutputDTO } from "./ProductVariantOutputDTO";

export interface MicrophonesVariantOutputDTO extends ProductVariantOutputDTO {
    color: string,
    microphoneId: number
}

export function isMicrophoneVariantOutputDTO(variant: ProductVariantOutputDTO): variant is MicrophonesVariantOutputDTO {
    return "microphoneId" in variant;
}

export function isMicrophoneVariantOutputDTOArray(variants: ProductVariantOutputDTO[]): variants is MicrophonesVariantOutputDTO[] {
    return variants.every(isMicrophoneVariantOutputDTO);
}