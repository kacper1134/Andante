package org.andante.orders.logic.service;

import org.andante.orders.dto.OrderQuerySpecification;
import org.andante.orders.enums.OrderSortingOrder;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderService {
    Optional<OrderOutput> getById(Long id);
    Set<OrderOutput> getAllByIds(List<Long> ids);
    Page<OrderOutput> getByClientAndStatus(String email, OrderStatus status, Integer page, Integer count, OrderSortingOrder sortingOrder);
    OrderOutput create(OrderInput orderInput);
    OrderOutput update(OrderInput orderInput);
    OrderOutput delete(Long id);
    Page<OrderOutput> getByQuery(OrderQuerySpecification orderQuerySpecification);
}
