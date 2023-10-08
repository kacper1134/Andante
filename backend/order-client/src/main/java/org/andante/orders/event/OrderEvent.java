package org.andante.orders.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.orders.dto.OrderOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {

    private OrderOutputDTO orders;
    private OperationType operationType;
}
