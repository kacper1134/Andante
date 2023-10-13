package org.andante.gramophones.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.gramophones.configuration.GramophonesTestConfiguration;
import org.andante.gramophones.dto.GramophonesInputDTO;
import org.andante.gramophones.dto.GramophonesOutputDTO;
import org.andante.gramophones.repository.GramophonesRepository;
import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.andante.gramophones.utils.GramophonesTestUtils;
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
@Import({GramophonesTestUtils.class, GramophonesTestConfiguration.class})
@Transactional
public class GramophonesControllerTest {

    private static final String GET_ALL_BY_ID_PATH = "/product/gramophones/bulk?ids=%s";
    private static final String CREATE_PATH = "/product/gramophones";
    private static final String UPDATE_PATH = "/product/gramophones";
    private static final String DELETE_PATH = "/product/gramophones/%d";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GramophonesRepository repository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private GramophonesTestUtils utils;

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
        @DisplayName("should return all requested gramophones when all validation criteria are met")
        void shouldReturnAllRequestedGramophonesWhenAllValidationCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            Set<GramophonesEntity> generatedGramophones = utils.generateGramophones(5);
            generatedGramophones.forEach(gramophones -> gramophones.setProducer(producer));

            List<GramophonesEntity> databaseResponse = repository.saveAll(generatedGramophones);

            Set<Long> identifiers = databaseResponse.stream()
                    .map(GramophonesEntity::getId)
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
            List<GramophonesOutputDTO> gramophones = readResponse(result);

            // then
            assertThat(gramophones).isNotNull().hasSameSizeAs(databaseResponse);
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
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesInputDTO gramophonesInputDTO = utils.toInputDTO(gramophones);
            gramophonesInputDTO.setId(-1L);
            String serializedInput = objectMapper.writeValueAsString(gramophonesInputDTO);

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
        @DisplayName("should create gramophones given all other criteria are met")
        void shouldCreateGramophonesGivenAllOtherCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesInputDTO gramophonesInputDTO = utils.toInputDTO(gramophones);
            utils.setValidData(gramophonesInputDTO);
            gramophonesInputDTO.setId(Math.abs(gramophonesInputDTO.getId()));

            String serializedInput = objectMapper.writeValueAsString(gramophonesInputDTO);
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
            GramophonesInputDTO gramophonesInputDTO = utils.toInputDTO(utils.generateGramophone());
            String serializedInput = objectMapper.writeValueAsString(gramophonesInputDTO);

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
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesEntity persistedGramophones = repository.save(gramophones);

            GramophonesInputDTO gramophonesInputDTO = utils.toInputDTO(persistedGramophones);
            utils.setValidData(gramophonesInputDTO);
            String serializedInput = objectMapper.writeValueAsString(gramophonesInputDTO);

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
        @DisplayName("should return bad request status when gramophones with requested identifiers do not exist")
        void shouldReturnBadRequestStatusWhenGramophonesWithRequestedIdentifiersDoNotExist() {
            // given
            Long identifier = Math.abs(utils.generate(Long.class));

            // when
            // always

            // then
            mockMvc.perform(delete(String.format(DELETE_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete gramophones when all validation criteria are valid")
        void shouldDeleteGramophonesWhenAllValidationCriteriaAreValid() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophonesToPersist = utils.generateGramophone();
            gramophonesToPersist.setProducer(producer);
            GramophonesEntity persistedGramophones = repository.save(gramophonesToPersist);

            Long identifier = persistedGramophones.getId();

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