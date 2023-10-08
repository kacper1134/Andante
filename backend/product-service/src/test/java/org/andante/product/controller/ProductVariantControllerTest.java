package org.andante.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.AmplifiersVariantRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.dto.ProductOrderVariantDTO;
import org.andante.product.dto.ProductOrderViolationDTO;
import org.andante.product.dto.ProductVariantOutputDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({AmplifiersTestUtils.class, AmplifiersTestConfiguration.class})
public class ProductVariantControllerTest {

    private static final String FIND_VARIANTS_PATH = "/product/variant/ids?ids=%s";
    private static final String FIND_VARIANT_PATH = "/product/variant/%s";
    private static final String VALIDATE_ORDER_PATH = "/product/variant/validate/order";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersVariantRepository amplifiersVariantRepository;

    @Autowired
    private AmplifiersTestUtils amplifiersTestUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Find Variants")
    class GetVariantsTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if requested identifiers list is empty")
        void shouldReturnPreconditionFailedStatusIfRequestedIdentifiersListIsEmpty() {
            // given
            String identifiersParameter = "";

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_VARIANTS_PATH, identifiersParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> validationMessages = readResponse(result);

            // then
            assertThat(validationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed given at least one identifier is not positive")
        void shouldReturnPreconditionFailedGivenAtLeastOneIdentifierIsNotPositive() {
            // given
            Set<Long> identifiers = Set.of(1L, -2L, 3L, -4L, 5L);
            String identifiersParameter = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_VARIANTS_PATH, identifiersParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all existing variants given all preconditions are met")
        void shouldReturnAllExistingVariantsGivenAllPreconditionsAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<AmplifiersVariantEntity> variants = amplifiersTestUtils.generate(AmplifiersVariantEntity.class, 5);
            variants.forEach(variant -> variant.setAmplifiers(persistedAmplifiers));

            List<AmplifiersVariantEntity> persistedVariants = amplifiersVariantRepository.saveAll(variants);

            Set<Long> identifiers = persistedVariants.stream()
                    .map(AmplifiersVariantEntity::getId)
                    .collect(Collectors.toSet());

            String identifiersParameter = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_VARIANTS_PATH, identifiersParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ProductVariantOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedVariants);
        }
    }

    @Nested
    @DisplayName("Find Variant")
    class FindVariantTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided identifier is negative")
        void shouldReturnPreconditionFailedStatusWhenProvidedIdentifierIsNegative() {
            // given
            Long identifier = -1L;

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_VARIANT_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotEmpty().hasSize(1);
        }

        @Test
        @SneakyThrows
        @DisplayName("should return not found status when variant with given identifier does not exist")
        void shouldReturnNotFoundStatusWhenVariantWithGivenIdentifierDoesNotExist() {
            // given
            Long identifier = 1L;

            // when
            mockMvc.perform(get(String.format(FIND_VARIANT_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return variant if it exists")
        void shouldReturnVariantIfItExists() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity amplifiersVariant = amplifiersTestUtils.generate(AmplifiersVariantEntity.class);
            amplifiersVariant.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = amplifiersVariantRepository.save(amplifiersVariant);

            // when
            mockMvc.perform(get(String.format(FIND_VARIANT_PATH, persistedVariant.getId()))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Validate Order")
    class ValidateOrderTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if set of objects to validate is empty")
        void shouldReturnPreconditionFailedStatusIfSetOfObjectsToValidateIsEmpty() {
            // given
            Set<ProductOrderVariantDTO> orderVariants = Set.of();
            String queryBody = objectMapper.writeValueAsString(orderVariants);

            // when
            MvcResult result = mockMvc.perform(post(VALIDATE_ORDER_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(queryBody))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<ProductOrderViolationDTO> violations = readResponse(result);

            // then
            assertThat(violations).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return MissingProductVariant violation type if variant does not exist")
        void shouldReturnMissingProductVariantViolationTypeIfVariantDoesNotExist() {
            // given
            Set<ProductOrderVariantDTO> productOrderVariants = Set.of(ProductOrderVariantDTO.builder()
                    .variantIdentifier(1L)
                    .orderedQuantity(5)
                    .build());

            String serializedContent = objectMapper.writeValueAsString(productOrderVariants);

            // when
            mockMvc.perform(post(VALIDATE_ORDER_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedContent))
                    .andExpect(status().isOk());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return empty set if order is valid")
        void shouldReturnEmptySetIfOrderIsValid() {
            // given
            int requestedQuantity = 5;
            int availableQuantity = 10;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity variant = amplifiersTestUtils.generate(AmplifiersVariantEntity.class);
            variant.setAmplifiers(persistedAmplifiers);
            variant.setAvailableQuantity(availableQuantity);

            AmplifiersVariantEntity persistedVariant = amplifiersVariantRepository.save(variant);

            Set<ProductOrderVariantDTO> productOrderVariantDTOs = Set.of(ProductOrderVariantDTO.builder()
                    .variantIdentifier(persistedVariant.getId())
                    .orderedQuantity(requestedQuantity)
                    .build());

            String serializedVariants = objectMapper.writeValueAsString(productOrderVariantDTOs);

            // when
            MvcResult result = mockMvc.perform(post(VALIDATE_ORDER_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedVariants))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ProductOrderViolationDTO> productOrderViolations = readResponse(result);

            // then
            assertThat(productOrderViolations).isNotNull().isEmpty();
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }
}
