package org.andante.orders.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.orders.dto.ClientDTO;
import org.andante.orders.dto.OrderOutputDTO;
import org.andante.orders.enums.DeliveryMethod;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class OrderOutput {

    private Long id;
    private LocalDateTime creationTimestamp;
    private Float deliveryCost;
    private DeliveryMethod deliveryMethod;
    private PaymentMethod paymentMethod;
    private ClientDTO client;
    private Location location;
    private Location deliveryLocation;
    private OrderStatus status;
    private Float paymentCost;
    private BigDecimal totalPrice;
    private Set<Long> orderEntriesIds;

    public OrderOutputDTO toDTO() {
        return OrderOutputDTO.builder()
                .id(id)
                .creationTimestamp(creationTimestamp)
                .deliveryCost(deliveryCost)
                .deliveryMethod(deliveryMethod)
                .paymentMethod(paymentMethod)
                .client(client)
                .location(location.toDTO())
                .deliveryLocation(deliveryLocation.toDTO())
                .status(status)
                .paymentCost(paymentCost)
                .totalCost(totalPrice)
                .orderEntriesIds(orderEntriesIds)
                .build();
    }

    public OrderInput toOrderInput() {
        return OrderInput.builder()
                .id(id)
                .clientId(client.getId())
                .locationId(location.getId())
                .orderEntriesIds(orderEntriesIds)
                .deliveryCost(deliveryCost)
                .deliveryMethod(deliveryMethod)
                .deliveryLocationId(deliveryLocation.getId())
                .paymentCost(paymentCost)
                .paymentMethod(paymentMethod)
                .status(status)
                .totalCost(totalPrice)
                .build();
    }
}
