package org.andante.orders.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.orders.dto.OrderQuerySpecification;
import org.andante.orders.enums.OrderSortingOrder;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.event.ProductOrderEvent;
import org.andante.orders.exception.OrderConflictException;
import org.andante.orders.exception.OrderNotFoundException;
import org.andante.orders.kafka.producer.KafkaOrderProducer;
import org.andante.orders.logic.mapper.OrderModelEntityMapper;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.andante.orders.logic.service.OrderService;
import org.andante.orders.repository.OrderRepository;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.andante.rsql.PersistentRSQLVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultOrderService implements OrderService {

    private static final String ORDER_CONFLICT_EXCEPTION_MESSAGE = "Order with identifier %d already exists";
    private static final String ORDER_NOT_FOUND_EXCEPTION_MESSAGE = "Order with identifier %d does not exist";

    private final OrderRepository orderRepository;
    private final OrderModelEntityMapper orderModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<OrderEntity> rsqlVisitor;
    private final KafkaOrderProducer kafkaOrderProducer;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<OrderOutput> getById(Long id) {
        Optional<OrderEntity> databaseResponse = orderRepository.findById(id);

        return databaseResponse.map(orderModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<OrderOutput> getAllByIds(List<Long> ids) {
        List<OrderEntity> databaseResponse = orderRepository.findAllById(ids);

        return databaseResponse.stream().map(orderModelEntityMapper::toModel).collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<OrderOutput> getByClientAndStatus(String email, OrderStatus status, Integer page, Integer count, OrderSortingOrder sortingOrder) {
        Pageable pageable = PageRequest.of(page, count, getSortingMethod(sortingOrder));

        Page<OrderEntity> databaseResponse = orderRepository.findAllByClientEmailAndStatus(email, status, pageable);

        return databaseResponse.map(orderModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderOutput create(OrderInput orderInput) {
        if (orderInput.getId() != null && orderRepository.existsById(orderInput.getId())) {
            throw new OrderConflictException(String.format(ORDER_CONFLICT_EXCEPTION_MESSAGE, orderInput.getId()));
        }

        OrderEntity orderToCreate = orderModelEntityMapper.toEntity(orderInput);
        OrderEntity orderCreated = orderRepository.save(orderToCreate);

        return orderModelEntityMapper.toModel(orderCreated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderOutput update(OrderInput orderInput) {
        if (orderInput.getId() == null || !orderRepository.existsById(orderInput.getId())) {
            throw new OrderNotFoundException(String.format(ORDER_NOT_FOUND_EXCEPTION_MESSAGE, orderInput.getId()));
        }

        OrderEntity orderToUpdate = orderModelEntityMapper.toEntity(orderInput);
        OrderEntity orderUpdated = orderRepository.save(orderToUpdate);
        return orderModelEntityMapper.toModel(orderUpdated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OrderOutput delete(Long id) {
        Optional<OrderEntity> databaseResponse = orderRepository.findById(id);

        if (databaseResponse.isEmpty()) {
            throw new OrderNotFoundException(String.format(ORDER_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        delete(databaseResponse.get());
        return orderModelEntityMapper.toModel(databaseResponse.get());
    }

    private void delete(OrderEntity orderEntity) {
        orderRepository.delete(orderEntity);

        orderEntity.getOrderEntries().stream()
                .map(this::buildEvent)
                .forEach(kafkaOrderProducer::publish);
    }

    private ProductOrderEvent buildEvent(OrderEntryEntity orderEntryEntity) {
        return ProductOrderEvent.builder()
                .variantId(orderEntryEntity.getProductVariantId())
                .orderedQuantityChange(-orderEntryEntity.getQuantity())
                .build();
    }

    private Pageable getPageSpecification(OrderQuerySpecification orderQuerySpecification) {
        return PageRequest.of(orderQuerySpecification.getPageNumber(), orderQuerySpecification.getPageSize(),
                getSortingMethod(orderQuerySpecification.getSortingOrder()));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED , readOnly = true)
    public Page<OrderOutput> getByQuery(OrderQuerySpecification orderQuerySpecification) {
        Node rootNode = rsqlParser.parse(orderQuerySpecification.getRsqlQuery());
        Specification<OrderEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = getPageSpecification(orderQuerySpecification);

        Page<OrderEntity> databaseResponse = orderRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(orderModelEntityMapper::toModel);
    }

    private Sort getSortingMethod(OrderSortingOrder orderSortingOrder) {
        switch (orderSortingOrder) {
            case NEWEST_FIRST:
                return Sort.by("creationTimestamp").descending();
            case OLDEST_FIRST:
                return Sort.by("creationTimestamp");
            default:
                return Sort.by("id");
        }
    }
}
