package org.andante.orders.logic.service;

import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderEntryOutput;

import java.util.Optional;
import java.util.Set;

public interface OrderEntryService {
    Optional<OrderEntryOutput> getById(Long id);
    Set<OrderEntryOutput> getAllByOrderId(Long orderId);
    OrderEntryOutput create(OrderEntryInput orderEntryInput);
    Set<OrderEntryOutput> bulkCreate(Set<OrderEntryInput> orderEntryInputs);
    OrderEntryOutput update(OrderEntryInput orderEntryInput);
    Optional<OrderEntryOutput> delete(Long id);
}
