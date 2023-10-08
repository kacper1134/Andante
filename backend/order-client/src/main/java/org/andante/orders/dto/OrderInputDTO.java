package org.andante.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andante.orders.enums.DeliveryMethod;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.enums.PaymentMethod;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInputDTO {

    @Positive(message = "Order identifier '${validatedValue}' must be a positive number")
    private Long id;

    @PositiveOrZero(message = "Order delivery cost must not be a negative number")
    @NotNull(message = "Order delivery cost must not be null")
    private Float deliveryCost;

    @NotNull(message = "Delivery method must be one of allowed values")
    private DeliveryMethod deliveryMethod;

    @NotNull(message = "Payment method must be one of allowed values")
    private PaymentMethod paymentMethod;

    @NotNull(message = "The user's id must not be null")
    @Positive(message = "The user's id '${validatedValue}' must be a positive number")
    private Long clientId;

    @Positive(message = "Location's identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Location's identifier must not be null")
    private Long locationId;

    @Positive(message = "Delivery location's identifier '${validatedValue}' must be positive")
    @NotNull(message = "Delivery location's identifier must not be null")
    private Long deliveryLocationId;

    @NotNull(message = "Order status must be one of allowed values")
    private OrderStatus status;

    @Min(value = 0, message = "Payment cost '${validatedValue}' must not be lesser than {value}")
    @NotNull(message = "Payment cost must not be null")
    private Float paymentCost;

    @NotNull(message = "Associated order entries '${validatedValue}' must not be a null value")
    private Set<@Positive(message = "Order entry identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Order entry identifier must not be null") Long> orderEntriesIds;
}
