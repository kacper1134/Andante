package org.andante.orders.utils;

import org.andante.order.configuration.OrderTestConfiguration;
import org.andante.orders.dto.ClientDTO;
import org.andante.orders.dto.LocationDTO;
import org.andante.orders.dto.OrderEntryInputDTO;
import org.andante.orders.dto.OrderInputDTO;
import org.andante.orders.enums.DeliveryMethod;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.enums.PaymentMethod;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Import(OrderTestConfiguration.class)
public class OrderTestUtils {

    private final EasyRandom generator;

    @Autowired
    public OrderTestUtils(@Qualifier("Order") EasyRandom generator) {
        this.generator = generator;
    }

    public Set<OrderEntity> generateOrder(int size) {
        Set<OrderEntity> orders = generator.objects(OrderEntity.class, size)
                .collect(Collectors.toSet());

        orders.forEach(order -> order.setOrderEntries(Set.of()));
        orders.forEach(order -> order.getOrderEntries()
                .forEach(orderEntry -> orderEntry.setProductVariantId(1L)));

        return orders;
    }

    public OrderEntity generateOrder() {
        OrderEntity order = generator.nextObject(OrderEntity.class);

        order.setOrderEntries(Set.of());
        order.getOrderEntries()
                .forEach(orderEntry -> orderEntry.setProductVariantId(Long.valueOf("1")));
        order.setClient(new ClientEntity());

        return order;
    }

    public <T> T generate(Class<T> type) {
        return generator.nextObject(type);
    }

    public <T> Set<T> generate(Class<T> type, int count) {
        return generator.objects(type, count).collect(Collectors.toSet());
    }

    public <T> T generateOne(Class<T> type) {
        return generator.nextObject(type);
    }

    public OrderInput toInput(OrderEntity order) {
        return OrderInput.builder()
                .id(order.getId())
                .deliveryCost(order.getDeliveryCost())
                .deliveryMethod(order.getDeliveryMethod())
                .paymentMethod(order.getPaymentMethod())
                .clientId(order.getClient().getId())
                .locationId(order.getLocation().getId())
                .deliveryLocationId(order.getDeliveryLocation().getId())
                .status(order.getOrderStatus())
                .paymentCost(order.getPaymentCost())
                .totalCost(new BigDecimal("90"))
                .orderEntriesIds(order.getOrderEntries().stream().map(OrderEntryEntity::getId).collect(Collectors.toSet()))
                .build();
    }

    public OrderEntryInput toInput(OrderEntryEntity orderEntry) {
        return OrderEntryInput.builder()
                .id(orderEntry.getId())
                .productVariantId(orderEntry.getProductVariantId())
                .quantity(orderEntry.getQuantity())
                .orderId(orderEntry.getOrder().getId())
                .build();
    }

    public OrderInputDTO toInputDTO(OrderEntity orderEntity) {
        return OrderInputDTO.builder()
                .id(orderEntity.getId())
                .deliveryCost(orderEntity.getDeliveryCost())
                .deliveryMethod(orderEntity.getDeliveryMethod())
                .paymentMethod(orderEntity.getPaymentMethod())
                .clientId(orderEntity.getClient().getId())
                .locationId(orderEntity.getLocation().getId())
                .status(orderEntity.getOrderStatus())
                .paymentCost(orderEntity.getPaymentCost())
                .build();
    }

    public OrderEntryInputDTO toInputDTO(OrderEntryEntity orderEntry) {
        return OrderEntryInputDTO.builder()
                .identifier(orderEntry.getId())
                .productVariantId(orderEntry.getProductVariantId())
                .quantity(orderEntry.getQuantity())
                .orderId(orderEntry.getOrder().getId())
                .build();
    }

    public OrderInputDTO setValidData(OrderInputDTO orderInputDTO) {
        orderInputDTO.setDeliveryCost(50.0F);
        orderInputDTO.setDeliveryMethod(DeliveryMethod.COURIER);
        orderInputDTO.setPaymentMethod(PaymentMethod.GOOGLE_PAY);
        orderInputDTO.setLocationId(1L);
        orderInputDTO.setStatus(OrderStatus.NEW);
        orderInputDTO.setPaymentCost(50.0F);
        orderInputDTO.setOrderEntriesIds(Set.of());

        return orderInputDTO;
    }

    public OrderEntryInputDTO buildValidEntry() {
        return OrderEntryInputDTO.builder()
                .identifier(1L)
                .productVariantId(1L)
                .quantity(1)
                .orderId(1L)
                .build();

    }

    public LocationDTO buildValidLocation(Set<Long> orderIds) {
        return LocationDTO.builder()
                .id(1L)
                .city("Wroclaw")
                .country("Poland")
                .flatNumber(1L)
                .postCode("12-123")
                .street("Kwiatowa")
                .streetNumber("3")
                .orderIds(orderIds)
                .build();
    }

    public ClientDTO buildValidClient() {
        return ClientDTO.builder()
                .emailAddress("user1@gmail.com")
                .name("Ewa")
                .phoneNumber("123456789")
                .surname("Surname")
                .orderIds(Set.of())
                .build();
    }
}
