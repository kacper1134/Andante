package org.andante.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.product.enums.ProductViolationType;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductOrderViolationDTO {

    private Long variantIdentifier;
    private ProductViolationType productViolationType;
    private String message;
}
