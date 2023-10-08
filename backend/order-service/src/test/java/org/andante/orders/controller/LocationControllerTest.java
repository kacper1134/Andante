package org.andante.orders.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.order.configuration.OrderTestConfiguration;
import org.andante.orders.dto.LocationDTO;
import org.andante.orders.enums.LocationSortingOrder;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.LocationRepository;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({OrderTestUtils.class, OrderTestConfiguration.class})
@Transactional
public class LocationControllerTest {

    private static final String FIND_LOCATION_PATH = "/order/location/%s";
    private static final String FIND_BY_QUERY_PATH = "/order/location/query?rsqlQuery=%s&pageNumber=%d&pageSize=%d&sortingOrder=%s";
    private static final String CREATE_COMMENT_PATH = "/order/location";
    private static final String UPDATE_COMMENT_PATH = "/order/location";
    private static final String DELETE_COMMENT_PATH = "/order/location/%d";

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
    @DisplayName("Find Location")
    class FindLocationTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided identifier is nonpositive value")
        void shouldReturnPreconditionFailedStatusWhenProvidedIdentifierIsNonPositiveValue() {
            // given
            Long identifier = -2L;
            String serializedIdentifier = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_LOCATION_PATH, serializedIdentifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return existing location with provided identifier")
        void shouldReturnExistingLocationWithProvidedIdentifier() {
            // given
            LocationEntity location = locationRepository.save(prepareLocation());
            location.setDeliveryOrders(Set.of());

            Long identifier = location.getId();

            String serializedIdentifier = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_LOCATION_PATH, serializedIdentifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            assertThat(result.getResponse().getContentAsString()).isNotNull();
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
        @DisplayName("should return location matching provided query")
        void shouldReturnLocationMatchingProvidedQuery() {
            // given
            String query = "id=gt=0";
            int page = 0;
            int pageSize = 3;
            LocationSortingOrder sortingOrder = LocationSortingOrder.ALPHABETICAL_STREET;

            LocationEntity location = prepareLocation();

            locationRepository.save(location);

            // when
            mockMvc.perform(get(String.format(FIND_BY_QUERY_PATH, query, page, pageSize, sortingOrder.name()))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Create Location")
    class CreateLocationTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if location is not valid")
        void shouldReturnPreconditionFailedStatusIfLocationIsNotValid() {
            // given
            LocationDTO locationToCreate = utils.generate(LocationDTO.class);
            String serializedContent = objectMapper.writeValueAsString(locationToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedContent))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should create location if DTO is valid")
        void shouldCreateLocationIfDTOIsValid() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            LocationDTO locationToCreate = utils.buildValidLocation(Set.of(persistedOrder.getId()));

            locationToCreate.setDeliveryOrdersIds(Set.of(persistedOrder.getId()));

            String serializedComment = objectMapper.writeValueAsString(locationToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedComment))
                    .andExpect(status().isOk())
                    .andReturn();

            Integer controllerResponse = readResponse(result);

            // then
            Optional<LocationEntity> createdLocation = locationRepository.findById((long)controllerResponse);

            assertThat(createdLocation).isPresent();
        }
    }

    @Nested
    @DisplayName("Update Location")
    class UpdateLocationTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if location is not valid")
        void shouldReturnPreconditionFailedStatusIfLocationIsNotValid() {
            // given
            LocationDTO locationToCreate = utils.generate(LocationDTO.class);
            String serializedContent = objectMapper.writeValueAsString(locationToCreate);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedContent))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should update location if DTO is valid")
        void shouldUpdateLocationIfDTOIsValid() {

            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());
            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            LocationDTO locationToCreate = utils.buildValidLocation(Set.of(persistedOrder.getId()));
            locationToCreate.setId(location.getId());
            locationToCreate.setDeliveryOrdersIds(Set.of(persistedOrder.getId()));

            String serializedComment = objectMapper.writeValueAsString(locationToCreate);

            // when
            mockMvc.perform(put(UPDATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedComment))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete Location")
    class DeleteLocationTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if provided identifier is negative")
        void shouldReturnPreconditionFailedStatusIfProvidedIdentifierIsNegative() {
            // given
            Long identifier = -1L;

            // when
            MvcResult result = mockMvc.perform(delete(String.format(DELETE_COMMENT_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if location with provided identifier does not exist")
        void shouldReturnBadRequestStatusIfLocationWithProvidedIdentifierDoesNotExist() {
            // given
            Long identifier = Math.abs(utils.generate(Long.class));

            // when
            mockMvc.perform(delete(String.format(DELETE_COMMENT_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete existing location")
        void shouldDeleteExistingLocation() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity locationToCreate = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(locationToCreate);
            order.setDeliveryLocation(locationToCreate);
            order.setOrderEntries(Set.of(prepareOrderEntryEntity()));
            OrderEntity persistedOrder = orderRepository.save(order);
            locationToCreate.setOrders(Set.of(persistedOrder));
            locationToCreate.setDeliveryOrders(Set.of(persistedOrder));

            Long identifier = locationToCreate.getId();

            // when
            mockMvc.perform(delete(String.format(DELETE_COMMENT_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            assertThat(locationRepository.findById(identifier)).isEmpty();
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
        location.setPostCode("12-123");
        location.setStreetNumber("12");
        location.setFlatNumber(12L);
        location.setOrders(Set.of());
        location.setDeliveryOrders(Set.of());
        return location;
    }

    private OrderEntryEntity prepareOrderEntryEntity(){
        OrderEntryEntity orderEntryEntity = utils.generate(OrderEntryEntity.class);
        orderEntryEntity.setId(1L);
        orderEntryEntity.setQuantity(1);
        orderEntryEntity.setProductVariantId(1L);
        return orderEntryEntity;
    }
}

