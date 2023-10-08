package org.andante.orders.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.orders.exception.OrderNotFoundException;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderEntryOutput;
import org.andante.orders.repository.OrderRepository;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderEntryModelEntityMapper {

    private static final String ORDER_NOT_FOUND_EXCEPTION_MESSAGE = "OrderEntry with identifier %d does not exist";

    private final OrderRepository orderRepository;
    private final OrderModelEntityMapper orderModelEntityMapper;


    public OrderEntryOutput toModel(OrderEntryEntity orderEntryEntity) {
        return OrderEntryOutput.builder()
                .id(orderEntryEntity.getId())
                .quantity(orderEntryEntity.getQuantity())
                .orderOutput(orderModelEntityMapper.toModel(orderEntryEntity.getOrder()))
                .productVariantId(orderEntryEntity.getProductVariantId())
                .build();
    }

    public Set<OrderEntryOutput> toModel(Set<OrderEntryEntity> orderEntryEntities) {
        return orderEntryEntities.stream().map(orderEntryEntity -> OrderEntryOutput.builder()
                .id(orderEntryEntity.getId())
                .quantity(orderEntryEntity.getQuantity())
                .orderOutput(orderModelEntityMapper.toModel(orderEntryEntity.getOrder()))
                .productVariantId(orderEntryEntity.getProductVariantId())
                .build()).collect(Collectors.toSet());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public OrderEntryEntity toEntity(OrderEntryInput orderEntryInput) {
        OrderEntity order = orderRepository.findById(orderEntryInput.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(String.format(ORDER_NOT_FOUND_EXCEPTION_MESSAGE, orderEntryInput.getOrderId())));

        return OrderEntryEntity.builder()
                .id(orderEntryInput.getId())
                .order(order)
                .productVariantId(orderEntryInput.getProductVariantId())
                .quantity(orderEntryInput.getQuantity())
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<OrderEntryEntity> toEntity(Set<OrderEntryInput> orderEntryInputs) {
        if(orderEntryInputs.isEmpty()) {
            throw new OrderNotFoundException(String.format(ORDER_NOT_FOUND_EXCEPTION_MESSAGE, -1));
        }
        Long orderId = orderEntryInputs.stream().findFirst().get().getOrderId();
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ORDER_NOT_FOUND_EXCEPTION_MESSAGE, orderId)));

        return orderEntryInputs.stream().map(orderEntryInput -> OrderEntryEntity.builder()
                .id(orderEntryInput.getId())
                .order(order)
                .productVariantId(orderEntryInput.getProductVariantId())
                .quantity(orderEntryInput.getQuantity())
                .build()).collect(Collectors.toSet());
    }
}
