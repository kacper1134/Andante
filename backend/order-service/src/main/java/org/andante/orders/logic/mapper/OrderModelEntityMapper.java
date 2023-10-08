package org.andante.orders.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.orders.controller.client.ProductClient;
import org.andante.orders.exception.ClientNotFoundException;
import org.andante.orders.exception.InternalOrderException;
import org.andante.orders.exception.LocationNotFoundException;
import org.andante.orders.exception.OrderCommunicationException;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.LocationRepository;
import org.andante.orders.repository.OrderEntryRepository;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.repository.entity.LocationEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.andante.product.dto.ProductVariantOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OrderModelEntityMapper {

    private static final String CLIENT_NOT_FOUND_EXCEPTION_MESSAGE = "Client with identifier %s does not exist";
    private static final String LOCATION_NOT_FOUND_EXCEPTION_MESSAGE = "Location with identifier %d does not exist";
    private static final String ORDER_COMMUNICATION_MESSAGE = "Something went wrong on product service";
    private static final String ORDER_INTERNAL_PROBLEM_MESSAGE = "Something went wrong on order service";


    private final LocationRepository locationRepository;
    private final ClientRepository clientRepository;
    private final OrderEntryRepository orderEntryRepository;
    private final LocationModelEntityMapper locationModelEntityMapper;
    private final ProductClient productClient;

    public OrderOutput toModel(OrderEntity orderEntity) {
        return OrderOutput.builder()
                .id(orderEntity.getId())
                .creationTimestamp(orderEntity.getCreationTimestamp())
                .deliveryCost(orderEntity.getDeliveryCost())
                .deliveryMethod(orderEntity.getDeliveryMethod())
                .paymentMethod(orderEntity.getPaymentMethod())
                .client(orderEntity.getClient().toModel().toDTO())
                .location(locationModelEntityMapper.toModel(orderEntity.getLocation()))
                .deliveryLocation(locationModelEntityMapper.toModel(orderEntity.getDeliveryLocation()))
                .status(orderEntity.getOrderStatus())
                .paymentCost(orderEntity.getPaymentCost())
                .totalPrice(calculateTotalPrice(orderEntity.getOrderEntries()))
                .orderEntriesIds(orderEntity.getOrderEntries().stream()
                        .map(OrderEntryEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public OrderEntity toEntity(OrderInput orderInput) {
        ClientEntity client = clientRepository.findById(orderInput.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(String.format(CLIENT_NOT_FOUND_EXCEPTION_MESSAGE, orderInput.getClientId())));
        LocationEntity location = locationRepository.findById(orderInput.getLocationId())
                .orElseThrow(() -> new LocationNotFoundException(String.format(LOCATION_NOT_FOUND_EXCEPTION_MESSAGE, orderInput.getLocationId())));
        LocationEntity deliveryLocation = locationRepository.findById(orderInput.getDeliveryLocationId())
                .orElseThrow(() -> new LocationNotFoundException(String.format(LOCATION_NOT_FOUND_EXCEPTION_MESSAGE, orderInput.getDeliveryLocationId())));

        Set<Long> orderEntriesIds = orderInput.getOrderEntriesIds();

        Set<OrderEntryEntity> orderEntries = Optional.ofNullable(orderEntriesIds).map(Set::size).orElse(0) > 0 ?
                new HashSet<>(orderEntryRepository.findAllById(orderEntriesIds)) : Set.of();

        return OrderEntity.builder()
                .id(orderInput.getId())
                .deliveryCost(orderInput.getDeliveryCost())
                .deliveryMethod(orderInput.getDeliveryMethod())
                .paymentMethod(orderInput.getPaymentMethod())
                .client(client)
                .location(location)
                .deliveryLocation(deliveryLocation)
                .orderStatus(orderInput.getStatus())
                .paymentCost(orderInput.getPaymentCost())
                .orderEntries(orderEntries)
                .build();
    }

    private BigDecimal calculateTotalPrice(Set<OrderEntryEntity> orderEntries) {
        if (orderEntries.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Long> productVariantsIds = orderEntries.stream()
                .map(OrderEntryEntity::getProductVariantId)
                .collect(Collectors.toSet());

        ResponseEntity<Set<ProductVariantOutputDTO>> productServiceResponse = productClient.getVariantsByIds(productVariantsIds);

        if (productServiceResponse == null || !productServiceResponse.hasBody()){
            throw new OrderCommunicationException(ORDER_COMMUNICATION_MESSAGE);
        }

        Map<Long, ProductVariantOutputDTO> productVariantToId = Objects.requireNonNull(productServiceResponse.getBody()).stream()
                .collect(Collectors.toMap(ProductVariantOutputDTO::getId, Function.identity()));

        List<BigDecimal> prices = orderEntries.stream()
                .map(orderEntry -> getOrThrow(productVariantToId, orderEntry.getProductVariantId()).getPrice().multiply(BigDecimal.valueOf(orderEntry.getQuantity())))
                .collect(Collectors.toList());

        BigDecimal result = BigDecimal.ZERO;

        for (BigDecimal price : prices) {
            result = result.add(price);
        }

        return prices.isEmpty() ? BigDecimal.ZERO : result;
    }

    private <K, V> V getOrThrow(Map<K, V> map, K key) {
        InternalOrderException internalOrderException = new InternalOrderException(ORDER_INTERNAL_PROBLEM_MESSAGE);
        return Optional.ofNullable(map.get(key)).orElseThrow(() -> internalOrderException);
    }
}
