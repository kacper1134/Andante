package org.andante.amplifiers.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.dto.AmplifiersInputDTO;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.ProducerEntity;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({AmplifiersTestUtils.class, AmplifiersTestConfiguration.class})
@Transactional
public class AmplifiersControllerTest {

    private static final String GET_ALL_BY_ID_PATH = "/product/amplifier/bulk?ids=%s";
    private static final String CREATE_PATH = "/product/amplifier";
    private static final String UPDATE_PATH = "/product/amplifier";
    private static final String DELETE_PATH = "/product/amplifier/%d";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmplifiersRepository repository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersTestUtils utils;

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
        @DisplayName("should return precondition failed status when at least one of provided identifiers is null")
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
        @DisplayName("should return all requested amplifiers when all validation criteria are met")
        void shouldReturnAllRequestedAmplifiersWhenAllValidationCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            Set<AmplifiersEntity> generatedAmplifiers = utils.generateAmplifiers(5);
            generatedAmplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            List<AmplifiersEntity> databaseResponse = repository.saveAll(generatedAmplifiers);

            Set<Long> identifiers = databaseResponse.stream()
                    .map(AmplifiersEntity::getId)
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
            ProducerEntity producer = utils.generate(ProducerEntity.class);
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersInputDTO amplifiersInputDTO = utils.toInputDTO(amplifiers);
            amplifiersInputDTO.setId(-1L);
            String serializedInput = objectMapper.writeValueAsString(amplifiersInputDTO);

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
        @DisplayName("should create amplifiers given all other criteria are met")
        void shouldCreateAmplifiersGivenAllOtherCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersInputDTO amplifiersInputDTO = utils.toInputDTO(amplifiers);
            utils.setValidData(amplifiersInputDTO);

            String serializedInput = objectMapper.writeValueAsString(amplifiersInputDTO);
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
        @DisplayName("should return bad request status for empty input")
        void shouldReturnBadRequestStatusForEmptyInput() {
            // given
            String serializedInput = "{}";

            // when
            // always

            // then
            mockMvc.perform(post(UPDATE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .content(serializedInput))
                    .andExpect(status().isPreconditionFailed());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return violation messages and precondition failed status for invalid input")
        void shouldReturnPreconditionFailedAndViolationMessagesForInvalidInput() {
            // given
            AmplifiersInputDTO amplifiersInputDTO = utils.toInputDTO(utils.generateAmplifier());
            String serializedInput = objectMapper.writeValueAsString(amplifiersInputDTO);

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
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity persistedAmplifiers = repository.save(amplifiers);

            AmplifiersInputDTO amplifiersInputDTO = utils.toInputDTO(persistedAmplifiers);
            utils.setValidData(amplifiersInputDTO);
            String serializedInput = objectMapper.writeValueAsString(amplifiersInputDTO);

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
        @DisplayName("should return bad request status when amplifiers with requested identifiers do not exist")
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
        @DisplayName("should delete amplifiers when all validation criteria are valid")
        void shouldDeleteAmplifiersWhenAllValidationCriteriaAreValid() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiersToPersist = utils.generateAmplifier();
            amplifiersToPersist.setProducer(producer);
            AmplifiersEntity persistedAmplifiers = repository.save(amplifiersToPersist);

            Long identifier = persistedAmplifiers.getId();

            // when
            mockMvc.perform(delete(String.format(DELETE_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            assertThat(repository.findById(identifier)).isEmpty();
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }
}