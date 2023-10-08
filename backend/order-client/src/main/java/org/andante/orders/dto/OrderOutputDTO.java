package org.andante.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.andante.orders.enums.DeliveryMethod;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderOutputDTO {

    private long id;
    private LocalDateTime creationTimestamp;
    private float deliveryCost;
    private DeliveryMethod deliveryMethod;
    private PaymentMethod paymentMethod;
    private ClientDTO client;
    private LocationDTO location;
    private LocationDTO deliveryLocation;
    private OrderStatus status;
    private Float paymentCost;
    private BigDecimal totalCost;
    private Set<Long> orderEntriesIds;
}
