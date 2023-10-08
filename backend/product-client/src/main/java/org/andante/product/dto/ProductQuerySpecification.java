package org.andante.product.dto;

import lombok.Builder;
import lombok.Data;
import org.andante.product.enums.ProductSortingOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class ProductQuerySpecification {

    @NotBlank(message = "Query '${validatedValue}' must not be a blank value")
    private String query;

    @PositiveOrZero(message = "Page number '${validatedValue}' must not be a negative number")
    @NotNull(message = "Page number must not be a null")
    private Integer pageNumber;

    @Positive(message = "Page size '${validatedValue}' must be a positive number")
    @NotNull(message = "Page size must not be a null")
    private Integer pageSize;

    @NotNull(message = "Product sorting order must be one of allowed values")
    private ProductSortingOrder sortingOrder;
}
