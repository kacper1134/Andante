package org.andante.orders.logic.service.impl;

import org.andante.orders.controller.client.ProductClient;
import org.andante.orders.exception.OrderEntryConflictException;
import org.andante.orders.exception.OrderEntryNotFoundException;
import org.andante.orders.logic.mapper.OrderEntryModelEntityMapper;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderEntryOutput;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.LocationRepository;
import org.andante.orders.repository.OrderEntryRepository;
import org.andante.orders.repository.OrderRepository;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.repository.entity.LocationEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.andante.orders.utils.OrderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(OrderTestUtils.class)
@Transactional
public class DefaultOrderEntryServiceTest {

    @Autowired
    private OrderTestUtils orderTestUtils;

    @Autowired
    private DefaultOrderEntryService defaultOrderEntryService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEntryRepository orderEntryRepository;

    @Autowired
    private OrderEntryModelEntityMapper orderEntryModelEntityMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LocationRepository locationRepository;

    @MockBean
    private ProductClient productClient;

    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnEmptySetForEmptyDatabase() {
            // given
            Long requestedIdentifier = orderTestUtils.generate(Long.class);

            // when
            Set<OrderEntryOutput> serviceResponse = defaultOrderEntryService.getAllByOrderId(requestedIdentifier);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing order entries")
        void shouldReturnAllExistingOrderEntries() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = orderTestUtils.generateOrder();
            order.setId(1L);
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);
            OrderEntity existingOrder = orderRepository.save(order);
            Set<OrderEntryEntity> orderEntries = Set.of(prepareOrderEntryEntity());
            orderEntries.forEach(entry -> entry.setOrder(existingOrder));
            List<OrderEntryEntity> existingEntries = orderEntryRepository.saveAll(orderEntries);
            Long identifier = existingOrder.getId();
            location.setDeliveryOrders(Set.of(existingOrder));

            // when
            Set<OrderEntryOutput> serviceResponse = defaultOrderEntryService.getAllByOrderId(identifier);

            Set<OrderEntryOutput> expectedResult = existingEntries.stream()
                    .map(orderEntryModelEntityMapper::toModel)
                    .collect(Collectors.toSet());

            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get All By Order Id")
    class GetAllByOrderId {

        @Test
        @DisplayName("should return empty set for non existing order")
        void shouldReturnEmptySetForNonExistingOrder() {
            // given
            Long identifier = 1L;

            // when
            Set<OrderEntryOutput> serviceResponse = defaultOrderEntryService.getAllByOrderId(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all order entries")
        void shouldReturnAllOrderEntries() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = orderTestUtils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);
            OrderEntity existingOrder = orderRepository.save(order);
            location.setDeliveryOrders(Set.of(existingOrder));
            Set<OrderEntryEntity> orderEntries = Set.of(prepareOrderEntryEntity());
            orderEntries.forEach(entry -> entry.setOrder(existingOrder));
            List<OrderEntryEntity> existingEntries = orderEntryRepository.saveAll(orderEntries);
            Long identifier = existingOrder.getId();

            // when
            Set<OrderEntryOutput> serviceResponse = defaultOrderEntryService.getAllByOrderId(identifier);

            Set<OrderEntryOutput> expectedResult = existingEntries.stream()
                    .map(orderEntryModelEntityMapper::toModel)
                    .collect(Collectors.toSet());

            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should raise OrderEntryConflictException when entry with given identifier already exists")
        void shouldRaiseDomainExceptionWhenEntryWithGivenIdentifierAlreadyExists() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = orderTestUtils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);
            OrderEntity generatedOrder = orderRepository.save(order);
            location.setDeliveryOrders(Set.of(generatedOrder));
            OrderEntryEntity entryToPersist = prepareOrderEntryEntity();
            entryToPersist.setOrder(generatedOrder);
            OrderEntryEntity persistedEntry = orderEntryRepository.save(entryToPersist);

            OrderEntryInput orderEntryInput = orderTestUtils.toInput(persistedEntry);

            // when
            // always

            // then
            assertThatThrownBy(() -> defaultOrderEntryService.create(orderEntryInput))
                    .isInstanceOf(OrderEntryConflictException.class);
        }

        @Test
        @DisplayName("should create entry given all criteria are met")
        void shouldCreateEntryGivenAllCriteriaAreMet() {
            // given
            mockProductClient();
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = orderTestUtils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);
            OrderEntity generatedOrder = orderRepository.save(order);
            location.setDeliveryOrders(Set.of(generatedOrder));


            OrderEntryEntity entryToCreate = prepareOrderEntryEntity();
            entryToCreate.setOrder(generatedOrder);

            OrderEntryInput orderEntryInput = orderTestUtils.toInput(entryToCreate);

            // when
            OrderEntryOutput serviceResponse = defaultOrderEntryService.create(orderEntryInput);

            OrderEntryOutput expectedResult = orderEntryModelEntityMapper.toModel(entryToCreate);
            expectedResult.setId(serviceResponse.getId());

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise OrderEntryNotFoundException when entry with given identifier does not exist")
        void shouldRaiseOrderEntryNotFoundExceptionWhenEntryWithGivenIdentifierDoesNotExist() {
            // given
            OrderEntryInput orderEntryInput = orderTestUtils.generate(OrderEntryInput.class);

            // when
            assertThat(orderEntryRepository.findById(orderEntryInput.getId())).isEmpty();

            // then
            assertThatThrownBy(() -> defaultOrderEntryService.update(orderEntryInput))
                    .isInstanceOf(OrderEntryNotFoundException.class);
        }

        @Test
        @DisplayName("should update order entry when all other criteria are met")
        void shouldUpdateOrderEntryWhenAllOtherCriteriaAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity orderToPersist = orderTestUtils.generateOrder();
            orderToPersist.setClient(client);
            orderToPersist.setLocation(location);
            orderToPersist.setDeliveryLocation(location);
            OrderEntity persistedOrder = orderRepository.save(orderToPersist);
            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderEntryEntity orderEntryEntity = prepareOrderEntryEntity();
            orderEntryEntity.setQuantity(2);
            orderEntryEntity.setOrder(persistedOrder);

            OrderEntryEntity persistedEntry = orderEntryRepository.save(orderEntryEntity);

            OrderEntryInput orderEntryInput = orderTestUtils.toInput(persistedEntry);

            // when
            OrderEntryOutput serviceResponse = defaultOrderEntryService.update(orderEntryInput);

            OrderEntryOutput expectedResult = orderEntryModelEntityMapper.toModel(persistedEntry);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should raise OrderEntryNotFoundException when entry with given identifier is missing")
        void shouldRaiseOrderEntryNotFoundExceptionWhenEntryWithGivenIdentifierIsMissing() {
            // given
            Long identifier = 1L;

            // when
            assertThat(orderEntryRepository.findById(identifier)).isEmpty();

            // then
            assertThatThrownBy(() -> defaultOrderEntryService.delete(identifier))
                    .isInstanceOf(OrderEntryNotFoundException.class);
        }

        @Test
        @DisplayName("should delete existing entry")
        void shouldDeleteExistingEntry() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity orderToPersist = orderTestUtils.generateOrder();
            orderToPersist.setId(1L);
            orderToPersist.setClient(client);
            orderToPersist.setLocation(location);
            orderToPersist.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(orderToPersist);
            OrderEntryEntity orderEntryToPersist = prepareOrderEntryEntity();
            orderEntryToPersist.setQuantity(2);
            orderEntryToPersist.setOrder(persistedOrder);
            orderEntryToPersist.setProductVariantId(1L);
            orderToPersist.setOrderEntries(Set.of(orderEntryToPersist));
            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderEntryEntity persistedEntry = orderEntryRepository.save(orderEntryToPersist);

            Long identifier = persistedEntry.getId();

            // when
            Optional<OrderEntryOutput> serviceResponse = defaultOrderEntryService.delete(identifier);

            OrderEntryOutput expectedResult = orderEntryModelEntityMapper.toModel(persistedEntry);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(Optional.of(expectedResult));
            assertThat(orderEntryRepository.findById(persistedEntry.getId())).isEmpty();
        }
    }

    private ClientEntity prepareClient(){
        ClientEntity client = orderTestUtils.generate(ClientEntity.class);
        client.setEmailAddress("user@gmail.com");
        client.setPhoneNumber("123456789");
        client.setOrders(Set.of());
        return client;
    }

    private LocationEntity prepareLocation(){
        LocationEntity location = orderTestUtils.generate(LocationEntity.class);
        location.setId(1L);
        location.setPostCode("12-123");
        location.setStreetNumber("12");
        location.setFlatNumber(12L);
        location.setOrders(Set.of());
        return location;
    }

    private OrderEntryEntity prepareOrderEntryEntity(){
        OrderEntryEntity orderEntryEntity = orderTestUtils.generate(OrderEntryEntity.class);
        orderEntryEntity.setId(1L);
        orderEntryEntity.setQuantity(1);
        orderEntryEntity.setProductVariantId(1L);
        return orderEntryEntity;
    }

    private void mockProductClient() {
        when(productClient.validateOrder(any())).thenReturn(ResponseEntity.ok(Set.of()));
    }
}
