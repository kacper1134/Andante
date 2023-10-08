package org.andante.orders.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.order.configuration.OrderTestConfiguration;
import org.andante.orders.dto.ClientDTO;
import org.andante.orders.repository.ClientRepository;
import org.andante.orders.repository.entity.ClientEntity;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({OrderTestUtils.class, OrderTestConfiguration.class})
@Transactional
public class UserControllerTest {

    private static final String FIND_USER_PATH = "/order/user/%s";
    private static final String CREATE_USER_PATH = "/order/user";
    private static final String UPDATE_USER_PATH = "/order/user";
    private static final String DELETE_USER_PATH = "/order/user/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderTestUtils utils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Find Location")
    class FindLocationTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided identifier is non positive")
        void shouldReturnPreconditionFailedStatusWhenProvidedIdentifierIsNonPositiveValue() {
            // given
            Long identifier = -1L;

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_USER_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return existing user with provided identifier")
        void shouldReturnExistingUserWithProvidedIdentifier() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());

            Long identifier = client.getId();

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_USER_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            assertThat(result.getResponse().getContentAsString()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Create user")
    class CreateUserTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if user is not valid")
        void shouldReturnPreconditionFailedStatusIfUserIsNotValid() {
            // given
            ClientDTO clientToCreate = utils.generate(ClientDTO.class);
            String serializedContent = objectMapper.writeValueAsString(clientToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_USER_PATH)
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
        @DisplayName("should create user if DTO is valid")
        void shouldCreateUserIfDTOIsValid() {
            // given
            ClientDTO clientToCreate = utils.buildValidClient();

            String serializedComment = objectMapper.writeValueAsString(clientToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_USER_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedComment))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            assertThat(result.getResponse().getContentAsString()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("Update user")
    class UpdateUserTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if user is not valid")
        void shouldReturnPreconditionFailedStatusIfUserIsNotValid() {
            // given
            ClientDTO clientToCreate = utils.generate(ClientDTO.class);
            String serializedContent = objectMapper.writeValueAsString(clientToCreate);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE_USER_PATH)
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
        @DisplayName("should update user if DTO is valid")
        void shouldUpdateUserIfDTOIsValid() {

            // given
            ClientEntity existingClient = clientRepository.save(prepareClient());

            ClientDTO clientToCreate = utils.buildValidClient();
            clientToCreate.setId(existingClient.getId());

            String serializedComment = objectMapper.writeValueAsString(clientToCreate);

            // when
            mockMvc.perform(put(UPDATE_USER_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedComment))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete user")
    class DeleteUserTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if provided identifier is not an email")
        void shouldReturnPreconditionFailedStatusIfProvidedIdentifierIsNotAnEmail() {
            // given
            Long identifier = 1L;

            // when
            MvcResult result = mockMvc.perform(delete(String.format(DELETE_USER_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if user with provided identifier does not exist")
        void shouldReturnBadRequestStatusIfUserWithProvidedIdentifierDoesNotExist() {
            // given
            String identifier = "anna@gmail.com";

            // when
            mockMvc.perform(delete(String.format(DELETE_USER_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete existing user")
        void shouldDeleteExistingClient() {
            // given
            ClientEntity client = clientRepository.save(prepareClient());

            Long identifier = client.getId();

            // when
            mockMvc.perform(delete(String.format(DELETE_USER_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            assertThat(clientRepository.findById(identifier)).isEmpty();
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    private ClientEntity prepareClient(){
        ClientEntity client = utils.generate(ClientEntity.class);
        client.setId(1L);
        client.setEmailAddress("user1@gmail.com");
        client.setPhoneNumber("123456789");
        client.setOrders(Set.of());
        return client;
    }
}
