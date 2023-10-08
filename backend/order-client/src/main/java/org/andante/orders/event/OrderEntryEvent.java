package org.andante.orders.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.orders.dto.OrderEntryOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntryEvent {

    private OrderEntryOutputDTO orderEntries;
    private OperationType operationType;
}
