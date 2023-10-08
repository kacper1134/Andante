package org.andante.orders.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.orders.event.ProductOrderEvent;
import org.andante.orders.exception.OrderEntryConflictException;
import org.andante.orders.exception.OrderEntryNotFoundException;
import org.andante.orders.kafka.producer.KafkaOrderProducer;
import org.andante.orders.logic.mapper.OrderEntryModelEntityMapper;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderEntryOutput;
import org.andante.orders.logic.service.OrderEntryService;
import org.andante.orders.logic.service.validator.ProductOrderValidator;
import org.andante.orders.repository.OrderEntryRepository;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultOrderEntryService implements OrderEntryService {

    private static final String ORDER_ENTRY_CONFLICT_EXCEPTION_MESSAGE = "Order entry with identifier %d already exists";
    private static final String ORDER_ENTRIES_CONFLICT_EXCEPTION_MESSAGE = "At least one of order entry with given id exists";
    private static final String ORDER_ENTRY_NOT_FOUND_EXCEPTION_MESSAGE = "Order entry with identifier %d does not exist";

    private final OrderEntryRepository orderEntryRepository;
    private final OrderEntryModelEntityMapper orderEntryModelEntityMapper;
    private final ProductOrderValidator productOrderValidator;
    private final KafkaOrderProducer kafkaOrderProducer;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<OrderEntryOutput> getById(Long id) {
        Optional<OrderEntryEntity> databaseResponse = orderEntryRepository.findById(id);

        return databaseResponse.map(orderEntryModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<OrderEntryOutput> getAllByOrderId(Long orderId) {
        Set<OrderEntryEntity> databaseResponse = orderEntryRepository.findAllByOrderId(orderId);

        return databaseResponse.stream().map(orderEntryModelEntityMapper::toModel).collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderEntryOutput create(OrderEntryInput orderEntryInput) {
        if (orderEntryInput.getId() != null && orderEntryRepository.existsById(orderEntryInput.getId())) {
            throw new OrderEntryConflictException(String.format(ORDER_ENTRY_CONFLICT_EXCEPTION_MESSAGE, orderEntryInput.getId()));
        }

        productOrderValidator.validateOrder(orderEntryInput.getProductVariantId(), orderEntryInput.getQuantity());

        OrderEntryEntity orderEntryToCreate = orderEntryModelEntityMapper.toEntity(orderEntryInput);
        OrderEntryEntity orderEntryCreated = orderEntryRepository.save(orderEntryToCreate);

        kafkaOrderProducer.publish(ProductOrderEvent.builder()
                .variantId(orderEntryInput.getProductVariantId())
                .orderedQuantityChange(orderEntryInput.getQuantity())
                .build());

        return orderEntryModelEntityMapper.toModel(orderEntryCreated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Set<OrderEntryOutput> bulkCreate(Set<OrderEntryInput> orderEntryInputs) {
        Set<Long> identifiers = orderEntryInputs.stream()
                .map(OrderEntryInput::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!identifiers.isEmpty() && !orderEntryRepository.findAllById(identifiers).isEmpty()) {
            throw new OrderEntryConflictException(ORDER_ENTRIES_CONFLICT_EXCEPTION_MESSAGE);
        }

        productOrderValidator.validateOrder(orderEntryInputs);

        Set<OrderEntryEntity> orderEntriesToCreate = orderEntryModelEntityMapper.toEntity(orderEntryInputs);
        Set<OrderEntryEntity> orderEntriesCreated = new HashSet<>(orderEntryRepository.saveAll(orderEntriesToCreate));

        orderEntryInputs.forEach(orderEntryInput -> kafkaOrderProducer.publish(ProductOrderEvent.builder()
                .variantId(orderEntryInput.getProductVariantId())
                .orderedQuantityChange(orderEntryInput.getQuantity())
                .build()));

        return orderEntryModelEntityMapper.toModel(orderEntriesCreated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderEntryOutput update(OrderEntryInput orderEntryInput) {
        if (orderEntryInput.getId() == null || !orderEntryRepository.existsById(orderEntryInput.getId())) {
            throw new OrderEntryNotFoundException(String.format(ORDER_ENTRY_NOT_FOUND_EXCEPTION_MESSAGE, orderEntryInput.getId()));
        }
        
        OrderEntryEntity databaseResponse = orderEntryRepository.findById(orderEntryInput.getId())
                .orElseThrow();

        int quantityChange = orderEntryInput.getQuantity() - databaseResponse.getQuantity();

        if (quantityChange > 0) {
            productOrderValidator.validateOrder(databaseResponse.getProductVariantId(), quantityChange);
        }

        OrderEntryEntity orderEntryToUpdate = orderEntryModelEntityMapper.toEntity(orderEntryInput);
        OrderEntryEntity orderEntryUpdated = orderEntryRepository.save(orderEntryToUpdate);

        kafkaOrderProducer.publish(ProductOrderEvent.builder()
                .variantId(orderEntryInput.getProductVariantId())
                .orderedQuantityChange(quantityChange)
                .build());

        return orderEntryModelEntityMapper.toModel(orderEntryUpdated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<OrderEntryOutput> delete(Long id) {
        Optional<OrderEntryEntity> databaseResponse = orderEntryRepository.findById(id);

        if (databaseResponse.isEmpty()) {
            throw new OrderEntryNotFoundException(String.format(ORDER_ENTRY_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        databaseResponse.ifPresent(this::delete);
        return databaseResponse.map(orderEntryModelEntityMapper::toModel);
    }

    private void delete(OrderEntryEntity orderEntryEntity) {
        orderEntryRepository.delete(orderEntryEntity);

        kafkaOrderProducer.publish(ProductOrderEvent.builder()
                .variantId(orderEntryEntity.getProductVariantId())
                .orderedQuantityChange(-orderEntryEntity.getQuantity())
                .build());
    }
}
