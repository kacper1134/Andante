package org.andante.orders.dto;

import lombok.Builder;
import lombok.Data;
import org.andante.orders.enums.OrderSortingOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class OrderQuerySpecification {

    @NotBlank(message = "Query '${validatedValue}' must not be a blank value")
    private String rsqlQuery;

    @PositiveOrZero(message = "Page number '${validatedValue}' must not be a negative number")
    @NotNull(message = "Page number must not be null")
    private Integer pageNumber;

    @Positive(message = "Page size '${validatedValue}' must be a positive number")
    @NotNull(message = "")
    private Integer pageSize;

    @NotNull(message = "Comment sorting order must be one of allowed values")
    private OrderSortingOrder sortingOrder;
}
