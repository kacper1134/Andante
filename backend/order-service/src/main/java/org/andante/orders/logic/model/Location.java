package org.andante.orders.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.orders.dto.LocationDTO;
import org.andante.orders.repository.entity.LocationEntity;

import java.util.Set;

@Builder
@Data
public class Location {

    private Long id;
    private String city;
    private String country;
    private Long flatNumber;
    private String postCode;
    private String street;
    private String streetNumber;
    private Set<Long> orderIds;
    private Set<Long> deliveryOrdersIds;

    public LocationDTO toDTO() {
        return LocationDTO.builder()
                .id(id)
                .city(city)
                .country(country)
                .flatNumber(flatNumber)
                .postCode(postCode)
                .street(street)
                .streetNumber(streetNumber)
                .orderIds(orderIds)
                .deliveryOrdersIds(deliveryOrdersIds)
                .build();
    }

    public LocationEntity toEntity() {
        return LocationEntity.builder()
                .id(id)
                .city(city)
                .country(country)
                .flatNumber(flatNumber)
                .postCode(postCode)
                .street(street)
                .streetNumber(streetNumber)
                .build();
    }
}
