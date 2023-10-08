package org.andante.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andante.product.dto.ProductVariantOutputDTO;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntryOutputDTO {

    private long id;
    private int quantity;
    private OrderOutputDTO order;
    private ProductVariantOutputDTO productVariant;
}
