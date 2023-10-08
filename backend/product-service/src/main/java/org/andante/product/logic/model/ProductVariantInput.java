package org.andante.product.logic.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.andante.product.repository.entity.ProductVariantEntity;

import java.math.BigDecimal;

@SuperBuilder
@Data
public abstract class ProductVariantInput {

    private Long id;
    private BigDecimal priceDifference;
    private Integer availableQuantity;
    private String thumbnailUrl;
    private String imageUrl;

    public abstract ProductVariantEntity toEntity();
}
