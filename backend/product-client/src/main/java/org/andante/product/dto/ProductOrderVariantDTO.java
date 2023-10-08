package org.andante.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductOrderVariantDTO {

    @Positive(message = "Product order variant identifier '${validatedValue}' must not be a positive number")
    @NotNull(message = "Product order variant identifier must not be a null")
    private Long variantIdentifier;

    @Positive(message = "Product order variant quantity '${validatedValue}' must not be a positive number")
    @NotNull(message = "Product ordered quantity must not be a null")
    private Integer orderedQuantity;
}
