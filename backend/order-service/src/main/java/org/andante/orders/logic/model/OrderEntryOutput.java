package org.andante.orders.logic.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderEntryOutput {

    private Long id;
    private Integer quantity;
    private OrderOutput orderOutput;
    private Long productVariantId;
}
