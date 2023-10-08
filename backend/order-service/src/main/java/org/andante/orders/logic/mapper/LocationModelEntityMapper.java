package org.andante.orders.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.orders.logic.model.Location;
import org.andante.orders.repository.OrderRepository;
import org.andante.orders.repository.entity.LocationEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LocationModelEntityMapper {

    private final OrderRepository orderRepository;

    public Location toModel(LocationEntity locationEntity) {
        return Location.builder()
                .id(locationEntity.getId())
                .city(locationEntity.getCity())
                .country(locationEntity.getCountry())
                .flatNumber(locationEntity.getFlatNumber())
                .postCode(locationEntity.getPostCode())
                .street(locationEntity.getStreet())
                .streetNumber(locationEntity.getStreetNumber())
                .orderIds(locationEntity.getOrders().stream()
                        .map(OrderEntity::getId)
                        .collect(Collectors.toSet()))
                .deliveryOrdersIds(locationEntity.getDeliveryOrders().stream()
                        .map(OrderEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public LocationEntity toEntity(Location location) {
        Set<OrderEntity> orders = new HashSet<>(orderRepository.findAllById(location.getOrderIds()));
        Set<OrderEntity> deliveryOrders = new HashSet<>(orderRepository.findAllById(location.getDeliveryOrdersIds()));

        return LocationEntity.builder()
                .id(location.getId())
                .country(location.getCountry())
                .city(location.getCity())
                .street(location.getStreet())
                .streetNumber(location.getStreetNumber())
                .flatNumber(location.getFlatNumber())
                .postCode(location.getPostCode())
                .orders(orders)
                .deliveryOrders(deliveryOrders)
                .build();
    }
}
