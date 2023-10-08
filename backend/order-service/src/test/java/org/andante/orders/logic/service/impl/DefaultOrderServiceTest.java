package org.andante.orders.logic.service.impl;

import org.andante.orders.dto.OrderQuerySpecification;
import org.andante.orders.enums.OrderSortingOrder;
import org.andante.orders.exception.OrderConflictException;
import org.andante.orders.exception.OrderNotFoundException;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.LocationRepository;
import org.andante.orders.repository.OrderRepository;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.repository.entity.LocationEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.utils.OrderTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(OrderTestUtils.class)
@Transactional
public class DefaultOrderServiceTest {

    @Autowired
    private OrderTestUtils orderTestUtils;

    @Autowired
    private DefaultOrderService service;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Nested
    @DisplayName("Get By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void whenRepositoryIsEmptyEmptySetIsReturned() {
            // given
            Long sampleIdentifier = 1L;
            // when
            Optional<OrderOutput> serviceResponse = service.getById(sampleIdentifier);
            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return existing order")
        void whenDatabaseEntryExistsItShouldBeReturned() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity orderToPersist = orderTestUtils.generate(OrderEntity.class);
            orderToPersist.setLocation(location);
            orderToPersist.setClient(client);
            orderToPersist.setOrderEntries(Set.of());
            orderToPersist.setDeliveryLocation(location);
            location.setDeliveryOrders(Set.of(orderToPersist));

            OrderEntity existingOrder = orderRepository.save(orderToPersist);

            Long identifier = existingOrder.getId();

            // when
            Optional<OrderOutput> serviceResponse = service.getById(identifier);

            // then
            assertThat(serviceResponse).isNotNull();
        }

    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should throw OrderConflictException when order with given identifier exists")
        void shouldRaiseExceptionWhenOrderWithGivenIdExist() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity orderToGenerate = orderTestUtils.generateOrder();
            orderToGenerate.setOrderEntries(Set.of());
            orderToGenerate.setClient(client);
            orderToGenerate.setLocation(location);
            orderToGenerate.setDeliveryLocation(location);
            OrderEntity databaseState = orderRepository.save(orderToGenerate);
            OrderInput orderInput = orderTestUtils.toInput(databaseState);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(orderInput))
                    .isInstanceOf(OrderConflictException.class);
        }

        @Test
        @DisplayName("should correctly create instance when all criteria are met")
        void shouldCorrectlyCreateInstanceWhenAllCriteriaAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity orderToGenerate = orderTestUtils.generateOrder();
            orderToGenerate.setClient(client);
            orderToGenerate.setLocation(location);
            orderToGenerate.setDeliveryLocation(location);
            location.setDeliveryOrders(Set.of(orderToGenerate));

            OrderInput orderInput = orderTestUtils.toInput(orderToGenerate);

            // when
            OrderOutput createdOrder = service.create(orderInput);

            // then
            assertThat(createdOrder).isNotNull();
            assertThat(createdOrder.getId()).isPositive();
        }

    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise OrderNotFoundException when order with given identifier does not exist")
        void shouldRaiseOrderNotFoundExceptionWhenEntityDoesNotExist() {
            // given
            OrderEntity order = orderTestUtils.generateOrder();
            OrderInput orderToUpdate = orderTestUtils.toInput(order);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.update(orderToUpdate))
                    .isInstanceOf(OrderNotFoundException.class);
        }

        @Test
        @DisplayName("should correctly update order given all other conditions are met")
        void shouldCorrectlyUpdateOrderWhenOtherConditionsAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity orderToUpdate = orderTestUtils.generateOrder();
            orderToUpdate.setOrderEntries(Set.of());
            orderToUpdate.setClient(client);
            orderToUpdate.setLocation(location);
            orderToUpdate.setDeliveryLocation(location);
            OrderEntity existingOrder = orderRepository.save(orderToUpdate);
            location.setDeliveryOrders(Set.of(existingOrder));

            OrderInput orderInput = orderTestUtils.toInput(existingOrder);

            // when
            OrderOutput serviceResponse = service.update(orderInput);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getId()).isPositive();
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should throw OrderNotFoundException when order with given identifier do not exist")
        void shouldThrowOrderNotFoundExceptionWhenOrderDoNotExist() {
            // given
            Long missingIdentifier = orderTestUtils.generate(Long.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(missingIdentifier))
                    .isInstanceOf(OrderNotFoundException.class);
        }

        @Test
        @DisplayName("should delete Order given all other criteria are met")
        void shouldDeleteOrderGivenAllOtherCriteriaAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity orderToDelete = orderTestUtils.generateOrder();
            orderToDelete.setOrderEntries(Set.of());
            orderToDelete.setClient(client);
            orderToDelete.setLocation(location);
            orderToDelete.setDeliveryLocation(location);
            OrderEntity order = orderRepository.save(orderToDelete);
            location.setDeliveryOrders(Set.of((order)));

            // when
            OrderOutput serviceResponse = service.delete(order.getId());

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(orderRepository.findById(order.getId())).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get By Query")
    class GetByQueryTests {

        @Test
        @DisplayName("should return no elements when page size is bigger than total amount of pages")
        void shouldReturnNoElementsWhenPageSizeIsBiggerThanTotalAmountOfPages() {
            // given
            int totalElements = 5;
            int pageNumber = 3;

            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            Set<OrderEntity> orders = orderTestUtils.generateOrder(totalElements);

            orders.forEach(order -> order.setClient(client));
            orders.forEach(order -> order.setLocation(location));
            orders.forEach(order -> order.setDeliveryLocation(location));

            orderRepository.saveAll(orders);

            OrderQuerySpecification querySpecification = OrderQuerySpecification.builder()
                    .rsqlQuery("id=gt=0")
                    .pageNumber(pageNumber)
                    .pageSize(totalElements)
                    .sortingOrder(OrderSortingOrder.NEWEST_FIRST)
                    .build();

            // when
            Page<OrderOutput> serviceResponse = service.getByQuery(querySpecification);

            // then
            assertThat(serviceResponse).isNotNull();
        }

        @Test
        @DisplayName("should return all elements when page size is bigger than total amount of existing products")
        void shouldReturnAllElementsWhenPageSizeIsBiggerThanTotalAmountOfExistingProducts() {
            // given
            int totalElements = 5;

            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            location.setDeliveryOrders(Set.of());

            Set<OrderEntity> orders = orderTestUtils.generateOrder(totalElements);
            orders.forEach(order -> order.setClient(client));
            orders.forEach(order -> order.setLocation(location));
            orders.forEach(order -> order.setDeliveryLocation(location));

            orderRepository.saveAll(orders);

            OrderQuerySpecification querySpecification = OrderQuerySpecification.builder()
                    .rsqlQuery("id=gt=0")
                    .pageNumber(0)
                    .pageSize(totalElements)
                    .sortingOrder(OrderSortingOrder.NEWEST_FIRST)
                    .build();

            // when
            Page<OrderOutput> serviceResponse = service.getByQuery(querySpecification);

            // then
            assertThat(serviceResponse).isNotNull();
        }

        @Test
        @DisplayName("should return elements sorted according to specified sorting order")
        void shouldReturnElementsSortedAccordingToSpecifiedSortingOrder() {
            // given
            int totalElements = 5;
            int pageNumber = 1;

            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            Set<OrderEntity> orders = orderTestUtils.generateOrder(totalElements);
            orders.forEach(order -> order.setClient(client));
            orders.forEach(order -> order.setLocation(location));
            orders.forEach(order -> order.setDeliveryLocation(location));

            orderRepository.saveAll(orders);

            OrderQuerySpecification querySpecification = OrderQuerySpecification.builder()
                    .rsqlQuery("id=gt=0")
                    .pageNumber(pageNumber)
                    .pageSize(totalElements)
                    .sortingOrder(OrderSortingOrder.NEWEST_FIRST)
                    .build();

            // when
            Page<OrderOutput> serviceResponse = service.getByQuery(querySpecification);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getContent()).isSortedAccordingTo(Comparator.comparing(OrderOutput::getCreationTimestamp));
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
}
