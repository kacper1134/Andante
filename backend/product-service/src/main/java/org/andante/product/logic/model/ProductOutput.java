package org.andante.product.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductOutputDTO;
import org.andante.product.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SuperBuilder
@Data
public abstract class ProductOutput {

    private Long id;
    private String name;
    private String description;
    private Float weight;
    private BigDecimal basePrice;
    private Long minimumFrequency;
    private Long maximumFrequency;
    private ProductType productType;
    @EqualsAndHashCode.Exclude private LocalDateTime creationTimestamp;
    @EqualsAndHashCode.Exclude private LocalDateTime modificationTimestamp;
    private List<Comment> comments;
    private Set<String> observers;
    private Producer producer;

    public abstract ProductOutputDTO toDTO();
}
