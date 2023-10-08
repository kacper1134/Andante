package org.andante.orders.logic.service.impl;

import org.andante.orders.exception.LocationConflictException;
import org.andante.orders.exception.LocationNotFoundException;
import org.andante.orders.logic.mapper.LocationModelEntityMapper;
import org.andante.orders.logic.model.Location;
import org.andante.orders.repository.LocationRepository;
import org.andante.orders.repository.entity.LocationEntity;
import org.andante.orders.utils.OrderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(OrderTestUtils.class)
@Transactional
public class DefaultLocationServiceTest {

    @Autowired
    private OrderTestUtils orderTestUtils;

    @Autowired
    private DefaultLocationService service;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationModelEntityMapper mapper;

    @Nested
    @DisplayName("Get By Id")
    class GetByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnNothingForEmptyDatabase() {
            // given
            Long id = orderTestUtils.generateOne(Long.class);

            // when
            Optional<Location> serviceResponse = service.getById(id);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return existing location")
        void shouldReturnExistingLocation() {
            // given
            LocationEntity persistedLocation = locationRepository.save(generateLocation());
            Long id = persistedLocation.getId();

            // when
            Optional<Location> serviceResponse = service.getById(id);

            // then
            assertThat(serviceResponse).isNotNull();
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should throw LocationConflictException if location with given identifier exists")
        void shouldThrowLocationConflictExceptionIfLocationWithGivenIdentifierExists() {
            // given
            LocationEntity location = locationRepository.save(generateLocation());

            Location locationToCreate = mapper.toModel(location);
            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(locationToCreate))
                    .isInstanceOf(LocationConflictException.class);
        }

        @Test
        @DisplayName("should create location if model is valid")
        void shouldCreateLocationIfModelIsValid() {
            // given
            Location locationToCreate = orderTestUtils.generate(Location.class);

            // when
            Location serviceResponse = service.create(locationToCreate);

            // then
            Optional<Location> persistedLocation = locationRepository.findById(serviceResponse.getId()).map(mapper::toModel);

            assertThat(persistedLocation).isPresent();
            assertThat(persistedLocation.get()).isEqualTo(serviceResponse);
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should throw LocationNotFoundException if location with given identifier does not exist")
        void shouldThrowLocationConflictExceptionIfLocationWithGivenIdentifierExists() {
            // given
            Location locationToCreate = orderTestUtils.generate(Location.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.modify(locationToCreate))
                    .isInstanceOf(LocationNotFoundException.class);
        }

        @Test
        @DisplayName("should update location if it exists")
        void shouldUpdateLocationIfItExists() {
            // given
            LocationEntity persistedLocation = locationRepository.save(generateLocation());

            Location expectedResult = mapper.toModel(persistedLocation);

            // when
            Location serviceResponse = service.modify(expectedResult);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should throw LocationNotFoundException if location does not exist")
        void shouldThrowLocationNotFoundExceptionIfLocationDoesNotExist() {
            // given
            Long id = Math.abs(orderTestUtils.generate(Long.class));

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(LocationNotFoundException.class)
                    .hasMessageContaining(String.valueOf(id));
        }

        @Test
        @DisplayName("should delete existing location")
        void shouldDeleteExistingLocation() {
            // given
            LocationEntity location = locationRepository.save(generateLocation());

            Location expectedResult = mapper.toModel(location);

            // when
            Optional<Location> serviceResponse = service.delete(location.getId());

            // then
            assertThat(locationRepository.findById(location.getId())).isEmpty();
            assertThat(serviceResponse).isNotNull().isEqualTo(Optional.of(expectedResult));
        }
    }

    private LocationEntity generateLocation(){
        LocationEntity locationEntity = orderTestUtils.generate(LocationEntity.class);
        locationEntity.setId(1L);
        locationEntity.setPostCode("12-123");
        locationEntity.setStreetNumber("12");
        locationEntity.setFlatNumber(12L);
        locationEntity.setOrders(Set.of());
        locationEntity.setDeliveryOrders(Set.of());
        return locationEntity;
    }
}
