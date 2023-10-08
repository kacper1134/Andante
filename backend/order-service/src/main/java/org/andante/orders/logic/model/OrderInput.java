package org.andante.orders.logic.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.andante.orders.enums.DeliveryMethod;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.Set;

@SuperBuilder
@Data
public class OrderInput {

    private Long id;
    private Float deliveryCost;
    private DeliveryMethod deliveryMethod;
    private PaymentMethod paymentMethod;
    private Long clientId;
    private Long locationId;
    private Long deliveryLocationId;
    private OrderStatus status;
    private Float paymentCost;
    private BigDecimal totalCost;
    private Set<Long> orderEntriesIds;
}
