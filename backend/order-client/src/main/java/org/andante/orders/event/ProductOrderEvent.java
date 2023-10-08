package org.andante.orders.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderEvent {

    private Long variantId;
    private Integer orderedQuantityChange;
}
