package org.andante.product.controller.mapper;

import org.andante.product.dto.ProductOrderViolationDTO;
import org.andante.product.logic.model.ProductOrderViolation;
import org.springframework.stereotype.Component;

@Component
public class ProductOrderViolationDTOModelMapper {

    public ProductOrderViolationDTO toDTO(ProductOrderViolation productOrderViolation) {
        return ProductOrderViolationDTO.builder()
                .variantIdentifier(productOrderViolation.getVariantIdentifier())
                .productViolationType(productOrderViolation.getProductViolationType())
                .message(productOrderViolation.getMessage())
                .build();
    }

    public ProductOrderViolation toModel(ProductOrderViolationDTO productOrderViolationDTO) {
        return ProductOrderViolation.builder()
                .variantIdentifier(productOrderViolationDTO.getVariantIdentifier())
                .productViolationType(productOrderViolationDTO.getProductViolationType())
                .message(productOrderViolationDTO.getMessage())
                .build();
    }
}
