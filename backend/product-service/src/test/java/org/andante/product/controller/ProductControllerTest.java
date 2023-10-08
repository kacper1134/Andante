package org.andante.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.dto.ProductOutputDTO;
import org.andante.product.enums.ProductSortingOrder;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
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
@Transactional
public class ProductControllerTest {

    private static final String GET_PRODUCTS_PATH = "/product/bulk?ids=%s";
    private static final String GET_BY_QUERY_PATH = "/product/query?query=%s&pageNumber=%d&pageSize=%d&sortingOrder=%s&rating=%d";
    private static final String CHANGE_STATUS_PATH = "/product/status?id=%s&user=%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersTestUtils amplifiersTestUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Get Products")
    class GetProductsTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status for empty identifiers set")
        void shouldReturnPreconditionFailedStatusForEmptyIdentifiersSet() {
            // given
            String identifiersParameter = "";

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_PRODUCTS_PATH, identifiersParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> validationMessages = readResponse(result);

            // then
            assertThat(validationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when at least one identifier is negative")
        void shouldReturnPreconditionFailedStatusWhenAtLeastOneIdentifierIsNegative() {
            // given
            Set<Long> identifiers = Set.of(1L, -2L, 3L, -4L, 5L);

            String identifiersParameter = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_PRODUCTS_PATH, identifiersParameter))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> validationMessages = readResponse(result);

            // then
            assertThat(validationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all existing products")
        void shouldReturnAllExistingProducts() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(5);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            List<AmplifiersEntity> persistedAmplifiers = amplifiersRepository.saveAll(amplifiers);

            Set<Long> identifiers = persistedAmplifiers.stream()
                    .map(AmplifiersEntity::getId)
                    .collect(Collectors.toSet());

            identifiers.add(Math.abs(amplifiersTestUtils.generate(Long.class)));

            String identifiersParameter = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.valueOf(String.format(GET_PRODUCTS_PATH, identifiersParameter)))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ProductOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedAmplifiers);
        }
    }

    @Nested
    @DisplayName("Get By Query")
    class GetByQueryTests {

        @Test
        @SneakyThrows
        @DisplayName("should return violation messages and bad request status when query specification is not valid")
        void shouldReturnViolationMessagesAndBadRequestStatusWhenSpecificationIsNotValid() {
            // given
            String query = "";
            int pageNumber = -1;
            int pageSize = 0;
            String sortingOrder = "";
            int minimumRating = 0;

            // when
            mockMvc.perform(get(String.format(GET_BY_QUERY_PATH, query, pageNumber, pageSize, sortingOrder, minimumRating))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return products matching query")
        void shouldReturnProductsMatchingQuery() {
            // given
            String query = "id=gt=0";
            int pageNumber = 0;
            int pageSize = 3;
            int minimumRating = 0;
            ProductSortingOrder sortingOrder = ProductSortingOrder.RECENTLY_ADDED;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(5);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            amplifiersRepository.saveAll(amplifiers);

            // when
            mockMvc.perform(get(String.format(GET_BY_QUERY_PATH, query, pageNumber, pageSize, sortingOrder.name(), minimumRating))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Change Observation Status")
    class ChangeObservationStatusTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when identifier is negative or email is not valid")
        void shouldReturnPreconditionFailedStatusWhenIdentifierIsNegativeOrEmailIsNotValid() {
            // given
            Long identifier = 1L;
            String emailAddress = "test";

            // when
            MvcResult result = mockMvc.perform(post(String.format(CHANGE_STATUS_PATH, identifier, emailAddress))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return not found status when product with given identifier does not exist")
        void shouldReturnNotFoundStatusWhenProductWithGivenIdentifierDoesNotExist() {
            // given
            Long identifier = Math.abs(amplifiersTestUtils.generate(Long.class));
            String emailAddress = "test@gmail.com";

            // when
            mockMvc.perform(post(String.format(CHANGE_STATUS_PATH, identifier, emailAddress))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("should add observer if product exists and it is not already observed")
        void shouldAddObserverIfProductExistsAndItIsNotAlreadyObserved() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Long identifier = persistedAmplifiers.getId();

            String observer = "test@gmail.com";

            // when
            mockMvc.perform(post(String.format(CHANGE_STATUS_PATH, identifier, observer))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            Optional<AmplifiersEntity> updatedAmplifiers = amplifiersRepository.findById(identifier);

            assertThat(updatedAmplifiers).isPresent();
            assertThat(updatedAmplifiers.get().getObservers()).contains(observer);
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }
}
