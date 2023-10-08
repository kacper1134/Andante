package org.andante.product.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.product.enums.ProductViolationType;

@Builder
@Data
public class ProductOrderViolation {

    private Long variantIdentifier;
    private ProductViolationType productViolationType;
    private String message;
}
