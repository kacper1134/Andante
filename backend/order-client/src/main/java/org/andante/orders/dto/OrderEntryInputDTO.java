package org.andante.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntryInputDTO {

    @Positive(message = "Order entry identifier '${validatedValue}' must be a positive number")
    private Long identifier;

    @Min(value = 1, message = "Product's quantity '${validatedValue}' must not be lesser than {value}")
    @NotNull(message = "Order entry identifier must not be null")
    private Integer quantity;

    @Positive(message = "Order's identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Order's identifier must not be null")
    private Long orderId;

    @Positive(message = "Product's variant identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Product's identifier must not be null")
    private Long productVariantId;
}
