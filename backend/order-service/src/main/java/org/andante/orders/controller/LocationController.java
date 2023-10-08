package org.andante.orders.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.orders.controller.mapper.LocationDTOModelMapper;
import org.andante.orders.dto.LocationDTO;
import org.andante.orders.dto.LocationQuerySpecification;
import org.andante.orders.logic.model.Location;
import org.andante.orders.logic.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/order/location")
@Validated
public class LocationController {

    private static final String POSITIVE_IDENTIFIER_ERROR_MESSAGE = "Location identifier '${validatedValue}' must be a positive value";
    private static final String NULL_IDENTIFIER_ERROR_MESSAGE = "Location identifier must not be null";

    private final LocationService locationService;
    private final LocationDTOModelMapper locationDTOModelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> get(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                           @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long id) {
        Optional<Location> serviceResponse = locationService.getById(id);
        Optional<LocationDTO> locationFound = serviceResponse.map(locationDTOModelMapper::toDTO);

        if (locationFound.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity.ok(locationFound.get());
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody LocationDTO locationToCreate) {
        Location model = locationDTOModelMapper.toModel(locationToCreate);
        Location serviceResponse = locationService.create(model);

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<Long> modify(@Valid @RequestBody LocationDTO locationToModify) {
        Location model = locationDTOModelMapper.toModel(locationToModify);
        Location serviceResponse = locationService.modify(model);

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                  @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long id) {
        Optional<Location> serviceResponse = locationService.delete(id);

        if (serviceResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/query")
    public ResponseEntity<Page<LocationDTO>> findByQuery(@Valid LocationQuerySpecification locationQuerySpecification) {
        Page<Location> serviceResponse = locationService.getByQuery(locationQuerySpecification);

        Page<LocationDTO> locations = serviceResponse.map(Location::toDTO);

        return ResponseEntity.ok(locations);
    }
}
