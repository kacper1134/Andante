package org.andante.orders.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.dto.AmplifiersInputDTO;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.order.configuration.OrderTestConfiguration;
import org.andante.orders.controller.client.ProductClient;
import org.andante.orders.dto.OrderEntryInputDTO;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({OrderTestUtils.class, OrderTestConfiguration.class})
@Transactional
public class OrderEntryControllerTest {

    private static final String GET_BY_ID = "/order/orderEntry/%s";
    private static final String GET_ALL_BY_ORDER_ID = "/order/orderEntry/bulk/order/%s";
    private static final String CREATE = "/order/orderEntry";
    private static final String UPDATE = "/order/orderEntry";
    private static final String DELETE = "/order/orderEntry/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderEntryRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderTestUtils utils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductClient productClient;

    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed when at least one of provided identifiers is not positive")
        void shouldReturnPreconditionFailedWhenAtLeastOneOfProvidedIdentifiersIsNotPositive() {
            // given
            Long identifier = -1L;

            String requestParameters = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_BY_ID, requestParameters))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();
            List<String> validationMessages = readResponse(result);

            // then
            assertThat(validationMessages).isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return existing entry")
        void shouldReturnExistingEntry() {
            // given
            mockProductClient();
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderEntryEntity entry = prepareOrderEntryEntity();
            entry.setOrder(persistedOrder);

            OrderEntryEntity persistedEntry = repository.save(entry);

            Long identifier = persistedEntry.getId();

            String requestParameters = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_BY_ID, requestParameters))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            assertThat(result.getResponse().getContentAsString()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get All By Order Id")
    class GetAllByOrderId {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when identifier is not positive")
        void shouldReturnBadRequestWhenIdentifierIsNotPositive() {
            // given
            Long identifier = -1L;

            // when
            mockMvc.perform(get(GET_ALL_BY_ORDER_ID, identifier)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return no entry when order with given id does not exist")
        void shouldReturnNoEntryWhenOrderWithGivenIdDoesNotExist() {
            // given
            Long identifier = 5L;
            assertThat(orderRepository.findById(identifier)).isEmpty();

            String requestParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ORDER_ID, requestParameter))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            List<AmplifiersVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all entries of existing order")
        void shouldReturnAllEntriesOfExistingOrder() {
            // given
            mockProductClient();
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            Set<OrderEntryEntity> entryEntities = Set.of(prepareOrderEntryEntity());
            entryEntities.forEach(variant -> variant.setOrder(persistedOrder));

            List<OrderEntryEntity> persistedEntries = repository.saveAll(entryEntities);

            Long identifier = persistedOrder.getId();
            String requestParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ORDER_ID, requestParameter))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<AmplifiersVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedEntries);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when at least one of constraints is violated")
        void shouldReturnBadRequestStatusWhenAtLeastOneOfConstraintsIsViolated() {
            // given
            AmplifiersInputDTO input = utils.generate(AmplifiersInputDTO.class);
            String serializedInput = objectMapper.writeValueAsString(input);

            // when
            mockMvc.perform(post(CREATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status and violation messages when order do not exist")
        void shouldReturnBadRequestStatusAndViolationMessagesWhenOrderDoNotExist() {
            // given
            mockProductClient();
            OrderEntryInputDTO entryToCreate = utils.buildValidEntry();
            String serializedVariant = objectMapper.writeValueAsString(entryToCreate);

            // when
            assertThat(repository.findById(entryToCreate.getOrderId())).isEmpty();

            MvcResult result = mockMvc.perform(post(CREATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedVariant))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should create entry when all criteria are met")
        void shouldCreateEntryWhenAllCriteriaAreMet() {
            // given
            mockProductClient();
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderEntryInputDTO orderEntryInput = utils.buildValidEntry();
            orderEntryInput.setOrderId(persistedOrder.getId());
            String serializedInput = objectMapper.writeValueAsString(orderEntryInput);

            // then
            MvcResult result = mockMvc.perform(post(CREATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isOk())
                    .andReturn();

            Integer identifier = readResponse(result);

            // then
            assertThat(repository.findAllByOrderId(persistedOrder.getId())).anyMatch(variant -> variant.getId().equals((long)identifier));
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when at least one of constraints is violated")
        void shouldReturnBadRequestStatusWhenAtLeastOneOfConstraintsIsViolated() {
            // given
            AmplifiersInputDTO input = utils.generate(AmplifiersInputDTO.class);
            String serializedInput = objectMapper.writeValueAsString(input);

            // when
            mockMvc.perform(put(UPDATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status and violation messages when variant do not exist")
        void shouldReturnBadRequestStatusAndViolationMessagesWhenVariantDoNotExist() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderEntryInputDTO entryInputDTO = utils.buildValidEntry();
            entryInputDTO.setOrderId(persistedOrder.getId());

            String serializedContent = objectMapper.writeValueAsString(entryInputDTO);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedContent))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should update entry when all criteria are met")
        void shouldUpdateEntryWhenAllCriteriaAreMet() {
            // given
            mockProductClient();
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));
            OrderEntryEntity orderEntry = prepareOrderEntryEntity();
            orderEntry.setOrder(persistedOrder);

            OrderEntryEntity persistedEntry = repository.save(orderEntry);

            OrderEntryInputDTO entryInputDTO = utils.buildValidEntry();
            entryInputDTO.setOrderId(persistedOrder.getId());
            entryInputDTO.setIdentifier(persistedEntry.getId());

            String serializedVariant = objectMapper.writeValueAsString(entryInputDTO);

            // when
            mockMvc.perform(put(UPDATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedVariant))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when identifier is not positive")
        void shouldReturnPreconditionFailedStatusWhenIdentifierIsNotPositive() {
            // given
            Long identifier = -1L;
            String pathParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(delete(String.format(DELETE, pathParameter))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when entry does not exist")
        void shouldReturnBadRequestStatusWhenEntryDoesNotExist() {
            // given
            Long identifier = 1L;
            String pathParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(delete(String.format(DELETE, pathParameter))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete entry when all criteria are met")
        void shouldDeleteEntryWhenAllCriteriaAreMet() {
            // given
            mockProductClient();
            ClientEntity client = clientRepository.save(prepareClient());
            LocationEntity location = locationRepository.save(prepareLocation());

            OrderEntity order = utils.generateOrder();
            order.setClient(client);
            order.setLocation(location);
            order.setDeliveryLocation(location);

            OrderEntity persistedOrder = orderRepository.save(order);

            location.setDeliveryOrders(Set.of(persistedOrder));

            OrderEntryEntity orderEntry = prepareOrderEntryEntity();
            orderEntry.setOrder(persistedOrder);

            OrderEntryEntity persistedEntry = repository.save(orderEntry);
            Long identifier = persistedEntry.getId();
            String pathParameter = String.valueOf(identifier);

            // when
            mockMvc.perform(delete(String.format(DELETE, pathParameter))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    private void mockProductClient() {
        when(productClient.validateOrder(any())).thenReturn(ResponseEntity.ok(Set.of()));
        when(productClient.getVariantsByIds(any())).thenReturn(ResponseEntity.ok(Set.of(utils.generate(AmplifiersVariantOutputDTO.class))));
    }

    private ClientEntity prepareClient(){
        ClientEntity client = utils.generate(ClientEntity.class);
        client.setEmailAddress("user1@gmail.com");
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
