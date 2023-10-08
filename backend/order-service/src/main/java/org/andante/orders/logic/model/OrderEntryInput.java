package org.andante.orders.logic.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderEntryInput {

    private Long id;
    private Integer quantity;
    private Long orderId;
    private Long productVariantId;
}
