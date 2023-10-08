package org.andante.amplifiers.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.dto.AmplifiersInputDTO;
import org.andante.amplifiers.dto.AmplifiersVariantInputDTO;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.AmplifiersVariantRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
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
public class AmplifiersVariantControllerTest {

    private static final String GET_ALL_BY_ID = "/product/amplifier/variant/bulk?ids=%s";
    private static final String GET_ALL_BY_AMPLIFIERS_ID = "/product/amplifier/variant/bulk/%s";
    private static final String CREATE = "/product/amplifier/variant";
    private static final String UPDATE = "/product/amplifier/variant";
    private static final String DELETE = "/product/amplifier/variant/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmplifiersVariantRepository repository;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

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

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<AmplifiersVariantEntity> variants = utils.generate(AmplifiersVariantEntity.class, 5);
            variants.forEach(variant -> variant.setAmplifiers(persistedAmplifiers));

            List<AmplifiersVariantEntity> persistedVariants = repository.saveAll(variants);

            Set<Long> identifiers = persistedVariants.stream()
                    .map(AmplifiersVariantEntity::getId)
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

            List<AmplifiersVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedVariants);
        }
    }

    @Nested
    @DisplayName("Get All By Amplifier Id")
    class GetAllByAmplifierId {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when identifier is not positive")
        void shouldReturnBadRequestWhenIdentifierIsNotPositive() {
            // given
            Long identifier = -1L;

            // when
            mockMvc.perform(get(GET_ALL_BY_AMPLIFIERS_ID, identifier)
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return no variants when amplifier with given id does not exist")
        void shouldReturnNoVariantsWhenAmplifierWithGivenIdDoesNotExist() {
            // given
            Long identifier = 5L;
            assertThat(amplifiersRepository.findById(identifier)).isEmpty();

            String requestParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_AMPLIFIERS_ID, requestParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
            List<AmplifiersVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all variants of existing amplifier")
        void shouldReturnAllVariantsOfExistingAmplifier() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<AmplifiersVariantEntity> variantEntities = utils.generate(AmplifiersVariantEntity.class, 5);
            variantEntities.forEach(variant -> variant.setAmplifiers(persistedAmplifiers));

            List<AmplifiersVariantEntity> persistedVariants = repository.saveAll(variantEntities);

            Long identifier = persistedAmplifiers.getId();
            String requestParameter = String.valueOf(identifier);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_BY_AMPLIFIERS_ID, requestParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<AmplifiersVariantOutputDTO> controllerResponse = readResponse(result);

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
        @DisplayName("should return bad request status and violation messages when amplifiers do not exist")
        void shouldReturnBadRequestStatusAndViolationMessagesWhenAmplifiersDoNotExist() {
            // given
            AmplifiersVariantInputDTO variantToCreate = utils.buildValidVariant();
            String serializedVariant = objectMapper.writeValueAsString(variantToCreate);

            // when
            assertThat(repository.findById(variantToCreate.getAmplifiersId())).isEmpty();

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

            AmplifiersEntity generatedAmplifiers = utils.generateAmplifier();
            generatedAmplifiers.setProducer(producer);
            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(generatedAmplifiers);

            AmplifiersVariantInputDTO amplifiersInput = utils.buildValidVariant();
            amplifiersInput.setAmplifiersId(persistedAmplifiers.getId());
            String serializedInput = objectMapper.writeValueAsString(amplifiersInput);

            // then
            MvcResult result = mockMvc.perform(post(CREATE)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedInput))
                    .andExpect(status().isOk())
                    .andReturn();

            Integer identifier = readResponse(result);

            // then
            assertThat(repository.findAllByAmplifiersId(persistedAmplifiers.getId())).anyMatch(variant -> variant.getId().equals((long)identifier));
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
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantInputDTO amplifiersVariantInputDTO = utils.buildValidVariant();
            amplifiersVariantInputDTO.setAmplifiersId(persistedAmplifiers.getId());

            String serializedContent = objectMapper.writeValueAsString(amplifiersVariantInputDTO);

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

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity amplifiersVariant = utils.generate(AmplifiersVariantEntity.class);
            amplifiersVariant.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = repository.save(amplifiersVariant);

            AmplifiersVariantInputDTO variantInputDTO = utils.buildValidVariant();
            variantInputDTO.setAmplifiersId(persistedAmplifiers.getId());
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
        @DisplayName("should delete amplifiers variant when all criteria are met")
        void shouldDeleteAmplifiersVariantWhenAllCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity amplifiersVariant = utils.generate(AmplifiersVariantEntity.class);
            amplifiersVariant.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = repository.save(amplifiersVariant);
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
