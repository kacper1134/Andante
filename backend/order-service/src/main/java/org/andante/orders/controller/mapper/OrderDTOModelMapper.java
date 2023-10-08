package org.andante.orders.controller.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.orders.dto.LocationDTO;
import org.andante.orders.dto.OrderInputDTO;
import org.andante.orders.dto.OrderOutputDTO;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class OrderDTOModelMapper {
    private final LocationDTOModelMapper locationDTOModelMapper;

    public OrderOutputDTO toDTO(OrderOutput orderOutput){
        LocationDTO locationDTO = locationDTOModelMapper.toDTO(orderOutput.getLocation());
        LocationDTO deliveryLocationDTO = locationDTOModelMapper.toDTO(orderOutput.getDeliveryLocation());

        return OrderOutputDTO.builder()
                .id(orderOutput.getId())
                .deliveryLocation(deliveryLocationDTO)
                .location(locationDTO)
                .creationTimestamp(orderOutput.getCreationTimestamp())
                .deliveryCost(orderOutput.getDeliveryCost())
                .deliveryMethod(orderOutput.getDeliveryMethod())
                .paymentMethod(orderOutput.getPaymentMethod())
                .paymentCost(orderOutput.getPaymentCost())
                .client(orderOutput.getClient())
                .status(orderOutput.getStatus())
                .totalCost(orderOutput.getTotalPrice())
                .orderEntriesIds(orderOutput.getOrderEntriesIds())
                .build();
    }

    public OrderInput toModel(OrderInputDTO orderInputDTO){
        return OrderInput.builder()
                .id(orderInputDTO.getId())
                .deliveryCost(orderInputDTO.getDeliveryCost())
                .deliveryMethod(orderInputDTO.getDeliveryMethod())
                .paymentMethod(orderInputDTO.getPaymentMethod())
                .clientId(orderInputDTO.getClientId())
                .locationId(orderInputDTO.getLocationId())
                .deliveryLocationId(orderInputDTO.getDeliveryLocationId())
                .status(orderInputDTO.getStatus())
                .paymentCost(orderInputDTO.getPaymentCost())
                .orderEntriesIds(orderInputDTO.getOrderEntriesIds())
                .build();
    }
}
