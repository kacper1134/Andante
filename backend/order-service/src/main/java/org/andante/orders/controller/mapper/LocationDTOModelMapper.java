package org.andante.orders.controller.mapper;

import org.andante.orders.dto.LocationDTO;
import org.andante.orders.logic.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationDTOModelMapper {

    public LocationDTO toDTO(Location location) {
        return location.toDTO();
    }

    public Location toModel(LocationDTO locationDTO) {
        return Location.builder()
                .id(locationDTO.getId())
                .city(locationDTO.getCity())
                .country(locationDTO.getCountry())
                .flatNumber(locationDTO.getFlatNumber())
                .postCode(locationDTO.getPostCode())
                .street(locationDTO.getStreet())
                .streetNumber(locationDTO.getStreetNumber())
                .orderIds(locationDTO.getOrderIds())
                .deliveryOrdersIds(locationDTO.getDeliveryOrdersIds())
                .build();
    }
}
