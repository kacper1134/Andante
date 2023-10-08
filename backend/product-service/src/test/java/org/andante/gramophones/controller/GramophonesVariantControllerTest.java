package org.andante.gramophones.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.gramophones.configuration.GramophonesTestConfiguration;
import org.andante.gramophones.dto.GramophonesInputDTO;
import org.andante.gramophones.dto.GramophonesVariantInputDTO;
import org.andante.gramophones.dto.GramophonesVariantOutputDTO;
import org.andante.gramophones.repository.GramophonesRepository;
import org.andante.gramophones.repository.GramophonesVariantRepository;
import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
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
public class GramophonesVariantControllerTest {

    private static final String GET_ALL_BY_ID = "/product/gramophone/variant/bulk?ids=%s";
    private static final String GET_ALL_BY_GRAMOPHONES_ID = "/product/gramophone/variant/bulk/%s";
    private static final String CREATE = "/product/gramophone/variant";
    private static final String UPDATE = "/product/gramophone/variant";
    private static final String DELETE = "/product/gramophone/variant/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GramophonesVariantRepository repository;

    @Autowired
    private GramophonesRepository gramophonesRepository;

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
            String serializedParameters = "";

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ID, serializedParameters))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();
            List<String> validationMessages = readResponse(result);

            // then
            assertThat(validationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed when at least one of provided identifiers is not positive")
        void shouldReturnPreconditionFailedWhenAtLeastOneOfProvidedIdentifiersIsNotPositive() {
            // given
            Set<Long> identifiers = Set.of(-1L, 2L, -3L, 4L, -5L);

            long nonPositiveIdentifiersCount = identifiers.stream()
                    .filter(identifier -> identifier <= 0)
                    .count();
            String requestParameters = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ID, requestParameters))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();
            List<String> validationMessages = readResponse(result);

            // then
            assertThat(validationMessages).hasSizeGreaterThanOrEqualTo((int)nonPositiveIdentifiersCount);
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all existing variants")
        void shouldReturnAllExistingVariants() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);

            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophones);

            Set<GramophonesVariantEntity> variants = utils.generate(GramophonesVariantEntity.class, 5);
            variants.forEach(variant -> variant.setGramophones(persistedGramophones));

            List<GramophonesVariantEntity> persistedVariants = repository.saveAll(variants);

            Set<Long> identifiers = persistedVariants.stream()
                    .map(GramophonesVariantEntity::getId)
                    .collect(Collectors.toSet());
            identifiers.addAll(utils.generate(Long.class, 5).stream()
                    .map(Math::abs)
                    .collect(Collectors.toSet()));

            String requestParameters = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_ID, requestParameters))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<GramophonesVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedVariants);
        }
    }

    @Nested
    @DisplayName("Get All By Gramophones Id")
    class GetAllByGramophonesId {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when identifier is not positive")
        void shouldReturnBadRequestWhenIdentifierIsNotPositive() {
            // given
            Long identifier = -1L;

            // when
            mockMvc.perform(get(GET_ALL_BY_GRAMOPHONES_ID, identifier)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return no variants when gramophones with given id does not exist")
        void shouldReturnNoVariantsWhenGramophoneWithGivenIdDoesNotExist() {
            // given
            Long identifier = 5L;
            assertThat(gramophonesRepository.findById(identifier)).isEmpty();

            String requestParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_GRAMOPHONES_ID, requestParameter))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            List<GramophonesVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all variants of existing gramophones")
        void shouldReturnAllVariantsOfExistingGramophones() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);

            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophones);

            Set<GramophonesVariantEntity> variantEntities = utils.generate(GramophonesVariantEntity.class, 5);
            variantEntities.forEach(variant -> variant.setGramophones(persistedGramophones));

            List<GramophonesVariantEntity> persistedVariants = repository.saveAll(variantEntities);

            Long identifier = persistedGramophones.getId();
            String requestParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_GRAMOPHONES_ID, requestParameter))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<GramophonesVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedVariants);
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
            GramophonesInputDTO input = utils.generate(GramophonesInputDTO.class);
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
        @DisplayName("should return bad request status and violation messages when gramophones do not exist")
        void shouldReturnBadRequestStatusAndViolationMessagesWhenGramophonesDoNotExist() {
            // given
            GramophonesVariantInputDTO variantToCreate = utils.buildValidVariant();
            String serializedVariant = objectMapper.writeValueAsString(variantToCreate);

            // when
            assertThat(repository.findById(variantToCreate.getGramophonesId())).isEmpty();

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
        @DisplayName("should create variant when all criteria are met")
        void shouldCreateVariantWhenAllCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            GramophonesEntity generatedGramophones = utils.generateGramophone();
            generatedGramophones.setProducer(producer);
            GramophonesEntity persistedGramophones = gramophonesRepository.save(generatedGramophones);

            GramophonesVariantInputDTO gramophonesInput = utils.buildValidVariant();
            gramophonesInput.setGramophonesId(persistedGramophones.getId());
            String serializedInput = objectMapper.writeValueAsString(gramophonesInput);

            // then
            MvcResult result = mockMvc.perform(post(CREATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isOk())
                    .andReturn();

            Integer identifier = readResponse(result);

            // then
            assertThat(repository.findAllByGramophonesId(persistedGramophones.getId())).anyMatch(variant -> variant.getId().equals((long)identifier));
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when at least one of constraints is violated")
        void shouldReturnPreconditionFailedStatusWhenAtLeastOneOfConstraintsIsViolated() {
            // given
            GramophonesVariantInputDTO input = utils.generate(GramophonesVariantInputDTO.class);
            String serializedInput = objectMapper.writeValueAsString(input);

            // when
            mockMvc.perform(put(UPDATE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedInput))
                    .andExpect(status().isPreconditionFailed());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status and violation messages when variant do not exist")
        void shouldReturnBadRequestStatusAndViolationMessagesWhenVariantDoNotExist() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);

            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophones);

            GramophonesVariantInputDTO gramophonesVariantInputDTO = utils.buildValidVariant();
            gramophonesVariantInputDTO.setGramophonesId(persistedGramophones.getId());

            String serializedContent = objectMapper.writeValueAsString(gramophonesVariantInputDTO);

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
        @DisplayName("should update variant when all criteria are met")
        void shouldUpdateVariantWhenAllCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);

            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophones);

            GramophonesVariantEntity gramophonesVariantEntity = utils.generate(GramophonesVariantEntity.class);
            gramophonesVariantEntity.setGramophones(persistedGramophones);

            GramophonesVariantEntity persistedVariant = repository.save(gramophonesVariantEntity);


            GramophonesVariantInputDTO variantInputDTO = utils.buildValidVariant();
            variantInputDTO.setGramophonesId(persistedGramophones.getId());
            variantInputDTO.setId(persistedVariant.getId());

            String serializedVariant = objectMapper.writeValueAsString(variantInputDTO);

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
        @DisplayName("should return bad request status when variant does not exist")
        void shouldReturnBadRequestStatusWhenVariantDoesNotExist() {
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
        @DisplayName("should delete gramophones variant when all criteria are met")
        void shouldDeleteGramophonesVariantWhenAllCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);

            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophones);

            GramophonesVariantEntity gramophonesVariant = utils.generate(GramophonesVariantEntity.class);
            gramophonesVariant.setGramophones(persistedGramophones);

            GramophonesVariantEntity persistedVariant = repository.save(gramophonesVariant);
            Long identifier = persistedVariant.getId();
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
}
