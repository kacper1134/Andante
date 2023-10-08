package org.andante.orders.logic.service;

import org.andante.orders.dto.LocationQuerySpecification;
import org.andante.orders.logic.model.Location;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface LocationService {
    Optional<Location> getById(Long id);
    Location create(Location location);
    Location modify(Location location);
    Optional<Location> delete(Long id);
    Page<Location> getByQuery(LocationQuerySpecification locationQuerySpecification);
}
