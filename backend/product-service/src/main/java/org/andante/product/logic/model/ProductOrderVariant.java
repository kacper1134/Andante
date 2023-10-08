package org.andante.product.logic.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductOrderVariant {

    private Long variantIdentifier;
    private Integer orderedQuantity;
}
