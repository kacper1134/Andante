package org.andante.product.controller.mapper;

import org.andante.product.dto.ProductOrderVariantDTO;
import org.andante.product.logic.model.ProductOrderVariant;
import org.springframework.stereotype.Component;

@Component
public class ProductOrderVariantDTOModelMapper {

    public ProductOrderVariantDTO toDTO(ProductOrderVariant productOrderVariant) {
        return ProductOrderVariantDTO.builder()
                .variantIdentifier(productOrderVariant.getVariantIdentifier())
                .orderedQuantity(productOrderVariant.getOrderedQuantity())
                .build();
    }

    public ProductOrderVariant toModel(ProductOrderVariantDTO productOrderVariantDTO) {
        return ProductOrderVariant.builder()
                .variantIdentifier(productOrderVariantDTO.getVariantIdentifier())
                .orderedQuantity(productOrderVariantDTO.getOrderedQuantity())
                .build();
    }
}
