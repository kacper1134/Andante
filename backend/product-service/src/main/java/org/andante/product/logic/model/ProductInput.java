package org.andante.product.logic.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.andante.product.enums.ProductType;

import java.math.BigDecimal;
import java.util.Set;

@SuperBuilder
@Data
public abstract class ProductInput {

    private Long id;
    private String name;
    private String description;
    private Float weight;
    private BigDecimal basePrice;
    private Long minimumFrequency;
    private Long maximumFrequency;
    private ProductType productType;
    private Set<Long> commentsIds;
    private Set<String> observers;
    private String producerName;
}
