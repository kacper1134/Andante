package org.andante.orders.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.orders.dto.LocationQuerySpecification;
import org.andante.orders.enums.LocationSortingOrder;
import org.andante.orders.exception.LocationConflictException;
import org.andante.orders.exception.LocationNotFoundException;
import org.andante.orders.logic.mapper.LocationModelEntityMapper;
import org.andante.orders.logic.model.Location;
import org.andante.orders.logic.service.LocationService;
import org.andante.orders.repository.LocationRepository;
import org.andante.orders.repository.entity.LocationEntity;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultLocationService implements LocationService {

    private static final String LOCATION_CONFLICT_EXCEPTION_MESSAGE = "Location with identifier %d already exists";
    private static final String LOCATION_NOT_FOUND_EXCEPTION_MESSAGE = "Location with identifier %d does not exist";

    private final LocationRepository locationRepository;
    private final LocationModelEntityMapper locationModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<LocationEntity> rsqlVisitor;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<Location> getById(Long id) {
        Optional<LocationEntity> databaseResponse = locationRepository.findById(id);

        return databaseResponse.map(locationModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Location create(Location location) {
        if (location.getId() != null && locationRepository.existsById(location.getId())) {
            throw new LocationConflictException(String.format(LOCATION_CONFLICT_EXCEPTION_MESSAGE, location.getId()));
        }

        LocationEntity locationToCreate = locationModelEntityMapper.toEntity(location);
        LocationEntity locationCreated = locationRepository.save(locationToCreate);

        return locationModelEntityMapper.toModel(locationCreated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Location modify(Location location) {
        if (location.getId() == null || !locationRepository.existsById(location.getId())) {
            throw new LocationNotFoundException(String.format(LOCATION_NOT_FOUND_EXCEPTION_MESSAGE, location.getId()));
        }

        LocationEntity locationToUpdate = locationModelEntityMapper.toEntity(location);
        LocationEntity locationUpdated = locationRepository.save(locationToUpdate);

        return locationModelEntityMapper.toModel(locationUpdated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Optional<Location> delete(Long id) {
        Optional<LocationEntity> databaseResponse = locationRepository.findById(id);
        if (databaseResponse.isEmpty()) {
            throw new LocationNotFoundException(String.format(LOCATION_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        databaseResponse.ifPresent(locationRepository::delete);
        return databaseResponse.map(locationModelEntityMapper::toModel);
    }

    private Pageable getPageSpecification(LocationQuerySpecification locationQuerySpecification) {
        return PageRequest.of(locationQuerySpecification.getPageNumber(), locationQuerySpecification.getPageSize(),
                getSortingMethod(locationQuerySpecification.getSortingOrder()));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED , readOnly = true)
    public Page<Location> getByQuery(LocationQuerySpecification locationQuerySpecification) {
        Node rootNode = rsqlParser.parse(locationQuerySpecification.getRsqlQuery());
        Specification<LocationEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = getPageSpecification(locationQuerySpecification);

        Page<LocationEntity> databaseResponse = locationRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(locationModelEntityMapper::toModel);
    }

    private Sort getSortingMethod(LocationSortingOrder locationSortingOrder) {
        switch (locationSortingOrder) {
            case ALPHABETICAL_STREET:
                return Sort.by("street");
            case ALPHABETICAL_POSTCODE:
                return Sort.by("postCode");
            default:
                return Sort.by("id");
        }
    }
}
