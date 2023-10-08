package org.andante.product.logic.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductVariantOutputDTO;

import java.math.BigDecimal;
import java.util.Set;

@SuperBuilder
@Data
public abstract class ProductVariantOutput {

    private Long id;
    private BigDecimal price;
    private Integer availableQuantity;
    private String imageUrl;
    private String thumbnailUrl;
    private Set<String> observers;
    private String productName;

    public abstract ProductVariantOutputDTO toDTO();
}
