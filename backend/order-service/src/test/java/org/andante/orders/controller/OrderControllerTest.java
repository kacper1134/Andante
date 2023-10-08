package org.andante.orders.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.order.configuration.OrderTestConfiguration;
import org.andante.orders.dto.OrderInputDTO;
import org.andante.orders.enums.OrderSortingOrder;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({OrderTestUtils.class, OrderTestConfiguration.class})
@Transactional
public class OrderControllerTest {

    private static final String GET_ALL_BY_ID_PATH = "/order/bulk/order?ids=%s";
    private static final String FIND_BY_QUERY_PATH = "/order/query?rsqlQuery=%s&pageNumber=%d&pageSize=%d&sortingOrder=%s";
    private static final String CREATE_PATH = "/order";
    private static final String UPDATE_PATH = "/order";
    private static final String DELETE_PATH = "/order/%d";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private OrderTestUtils utils;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when empty set is provided")
        void shouldReturnPreconditionFailedStatusWhenEmptySetIsProvided() {
            // given
            String identifiersString = "";

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ID_PATH, identifiersString))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> response = readResponse(result);

            // then
            assertThat(response).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when at least one of provided identifier is null")
        void shouldReturnPreconditionFailedStatusWhenAtLeastOneOfIdentifiersIsNegative() {
            // given
            Set<Long> identifiers = Set.of(1L, -2L, 3L, -4L);

            String identifiersString = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            int negativeIdentifiersCount = (int)identifiers.stream()
                    .filter(val -> val <= 0)
                    .count();

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ID_PATH, identifiersString))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();
            List<String> response = readResponse(result);

            // then
            assertThat(response).isNotNull().hasSize(negativeIdentifiersCount);
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all requested orders when all validation criteria are met")
        void shouldReturnAllRequestedOrdersWhenAllValidationCriteriaAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            Set<OrderEntity> generatedOrders = utils.generateOrder(5);
            generatedOrders.forEach(amplifier -> amplifier.setClient(client));
            generatedOrders.forEach(amplifier -> amplifier.setLocation(location));
            generatedOrders.forEach(amplifier -> amplifier.setDeliveryLocation(location));

            List<OrderEntity> databaseResponse = orderRepository.saveAll(generatedOrders);
            location.setDeliveryOrders(new HashSet<>(databaseResponse));

            Set<Long> identifiers = databaseResponse.stream()
                    .map(OrderEntity::getId)
                    .collect(Collectors.toSet());
            identifiers.add(Math.abs(utils.generate(Long.class)));

            String identifiersString = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ID_PATH, identifiersString))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            List<AmplifiersOutputDTO> amplifiers = readResponse(result);

            // then
            assertThat(amplifiers).isNotNull().hasSameSizeAs(databaseResponse);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request for empty input")
        void shouldReturnPreconditionedFailedStatusAndMessageForEachOfMissingFields() {
            // given
            String serializedInput = "{}";

            // when
            // always

            // then
            mockMvc.perform(post(CREATE_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(serializedInput))
                    .andExpect(status().isPreconditionFailed());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return violation messages and precondition failed status for entity with negative identifier")
        void shouldReturnPreconditionFailedStatusAndViolationMessageForNegativeIdentifier() {
            // given
            ClientEntity client = prepareClient();
            LocationEntity location = prepareLocation();
            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            OrderInputDTO orderInputDTO = utils.toInputDTO(order);
            orderInputDTO.setId(-1L);
            String serializedInput = objectMapper.writeValueAsString(orderInputDTO);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        @SneakyThrows
        @DisplayName("should create order given all other criteria are met")
        void shouldCreateOrderGivenAllOtherCriteriaAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = utils.generateOrder();
            order.setId(12L);
            order.setClient(client);
            order.setLocation(location);
            OrderInputDTO orderInputDTO = utils.toInputDTO(order);
            orderInputDTO.setOrderEntriesIds(Set.of());
            orderInputDTO.setLocationId(location.getId());
            orderInputDTO.setDeliveryLocationId(location.getId());
            location.setDeliveryOrders(Set.of(order));

            String serializedInput = objectMapper.writeValueAsString(orderInputDTO);
            // when
            MvcResult result = mockMvc.perform(post(CREATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isOk())
                    .andReturn();
            Integer response = readResponse(result);

            // then
            assertThat(response).isNotNull().isPositive();
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return violation messages and precondition failed status for invalid input")
        void shouldReturnPreconditionFailedAndViolationMessagesForInvalidInput() {
            // given
            OrderInputDTO orderInputDTO = utils.toInputDTO(utils.generateOrder());
            String serializedInput = objectMapper.writeValueAsString(orderInputDTO);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();
            List<String> response = readResponse(result);

            // then
            assertThat(response).isNotNull().hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        @SneakyThrows
        @DisplayName("should update entity when it exists and all other criteria are met")
        void shouldUpdateEntityWhenItExistsAndAllOtherCriteriaAreMet() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = utils.generateOrder();
            order.setLocation(location);
            order.setDeliveryLocation(location);
            order.setClient(client);
            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderInputDTO orderInputDTO = utils.toInputDTO(persistedOrder);
            orderInputDTO.setOrderEntriesIds(Set.of());
            orderInputDTO.setDeliveryLocationId(location.getId());
            String serializedInput = objectMapper.writeValueAsString(orderInputDTO);

            // when
            // always

            // then
            mockMvc.perform(put(UPDATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @SneakyThrows
        @DisplayName("should return preconditioned failed status when non-positive identifier is provided")
        void shouldReturnPreconditionFailedStatusWhenNonPositiveIdentifierIsProvided() {
            // given
            Long identifier = -1L;

            // when
            // always

            // then
            mockMvc.perform(delete(String.format(DELETE_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when order with requested identifier do not exist")
        void shouldReturnBadRequestStatusWhenAmplifiersWithRequestedIdentifiersDoNotExist() {
            // given
            Long identifier = 1L;

            // when
            // always

            // then
            mockMvc.perform(delete(String.format(DELETE_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete order when all validation criteria are valid")
        void shouldDeleteOrderWhenAllValidationCriteriaAreValid() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = utils.generateOrder();
            order.setLocation(location);
            order.setClient(client);
            order.setDeliveryLocation(location);
            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            Long identifier = persistedOrder.getId();

            // when
            mockMvc.perform(delete(String.format(DELETE_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            assertThat(orderRepository.findById(identifier)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find By Query")
    class FindByQueryTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if query is not valid")
        void shouldReturnBadRequestStatusIfQueryIsNotValid() {
            // given
            String query = "";
            int page = -1;
            int pageSize = -1;
            String sortingOrder = "";

            // when
            mockMvc.perform(get(String.format(FIND_BY_QUERY_PATH, query, page, pageSize, sortingOrder))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all orders matching provided query")
        void shouldReturnAllOrdersMatchingProvidedQuery() {
            // given
            String query = "id=gt=0";
            int page = 0;
            int pageSize = 3;
            OrderSortingOrder sortingOrder = OrderSortingOrder.NEWEST_FIRST;

            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            Set<OrderEntity> orders = utils.generate(OrderEntity.class, 5);
            orders.forEach(order -> order.setLocation(location));
            orders.forEach(order -> order.setDeliveryLocation(location));
            orders.forEach(order -> order.setClient(client));
            orders.forEach(order -> order.setOrderEntries(Set.of()));
            List<OrderEntity> persistedOrders = orderRepository.saveAll(orders);
            location.setDeliveryOrders(new HashSet<>(persistedOrders));

            // when
            mockMvc.perform(get(String.format(FIND_BY_QUERY_PATH, query, page, pageSize, sortingOrder.name()))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    private ClientEntity prepareClient(){
        ClientEntity client = utils.generate(ClientEntity.class);
        client.setEmailAddress("user@gmail.com");
        client.setPhoneNumber("123456789");
        client.setOrders(Set.of());
        return client;
    }

    private LocationEntity prepareLocation(){
        LocationEntity location = utils.generate(LocationEntity.class);
        location.setId(1L);
        location.setPostCode("12-123");
        location.setStreetNumber("12");
        location.setFlatNumber(12L);
        location.setOrders(Set.of());
        return location;
    }
}
