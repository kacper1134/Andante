package org.andante.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.dto.ProducerDTO;
import org.andante.product.dto.ProductOutputDTO;
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
public class ProducerControllerTest {

    private static final String GET_ALL_PATH = "/product/producer/all?names=%s";
    private static final String GET_PRODUCTS_PATH = "/product/producer/%s";
    private static final String CREATE_PATH = "/product/producer";
    private static final String UPDATE_PATH = "/product/producer";
    private static final String DELETE_PATH = "/product/producer/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersTestUtils utils;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }

    @Nested
    @DisplayName("Get All")
    class GetAllTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided set of identifiers is empty")
        void shouldReturnPreconditionFailedStatusWhenProvidedSetOfIdentifiersIsEmpty() {
            // given
            String serializedIdentifiers = "";

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ALL_PATH, serializedIdentifiers))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all existing producers with given identifiers")
        void shouldReturnAllExistingProducersWithGivenIdentifiers() {
            // given
            List<ProducerEntity> producers = producerRepository.saveAll(utils.generate(ProducerEntity.class, 5));
            Set<String> producersNames = producers.stream()
                    .map(ProducerEntity::getName)
                    .collect(Collectors.toSet());

            producersNames.add(utils.generate(String.class));

            String joinedIdentifiers = String.join(",", producersNames);

            // when
            mockMvc.perform(get(String.format(GET_ALL_PATH, joinedIdentifiers))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Get Products")
    class GetProductsTests {

        @Test
        @SneakyThrows
        @DisplayName("should return empty set when producer does not exist")
        void shouldReturnEmptySetWhenProducerDoesNotExist() {
            // given
            String producerName = utils.generate(String.class);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_PRODUCTS_PATH, producerName))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ProductOutputDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().isEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all producer's products")
        void shouldReturnAllProducersProducts() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = utils.generateAmplifiers(5);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            amplifiersRepository.saveAll(amplifiers);

            String producerName = producer.getName();

            // when
            mockMvc.perform(get(String.format(GET_PRODUCTS_PATH, producerName))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when DTO is not valid")
        void shouldReturnPreconditionFailedStatusWhenDTOIsNotValid() {
            // given
            ProducerDTO producerToCreate = utils.generate(ProducerDTO.class);
            String serializedProducer = objectMapper.writeValueAsString(producerToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedProducer))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when producer with given name already exists")
        void shouldReturnBadRequestStatusWhenProducerWithGivenNameAlreadyExists() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            ProducerDTO producerDTO = utils.buildValidProducer();
            producerDTO.setName(producer.getName());

            String serializedProducer = objectMapper.writeValueAsString(producerDTO);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedProducer))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should create producer if DTO is valid")
        void shouldCreateProducerIfDTOIsValid() {
            // given
            ProducerDTO producerToCreate = utils.buildValidProducer();

            String serializedProducer = objectMapper.writeValueAsString(producerToCreate);

            // when
            mockMvc.perform(post(CREATE_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedProducer))
                    .andExpect(status().isOk());

            // then
            assertThat(producerRepository.findById(producerToCreate.getName())).isPresent();
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when DTO is not valid")
        void shouldReturnPreconditionFailedStatusWhenDTOIsNotValid() {
            // given
            ProducerDTO producerToCreate = utils.generate(ProducerDTO.class);
            String serializedProducer = objectMapper.writeValueAsString(producerToCreate);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedProducer))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status when producer with given name does not exist")
        void shouldReturnBadRequestStatusWhenProducerWithGivenNameDoesNotExist() {
            // given
            ProducerDTO producerDTO = utils.buildValidProducer();
            producerDTO.setName(utils.generate(String.class));

            String serializedProducer = objectMapper.writeValueAsString(producerDTO);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedProducer))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should update existing producer if model is valid")
        void shouldUpdateExistingProducerIfModelIsValid() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            ProducerDTO producerToUpdate = utils.buildValidProducer();
            producerToUpdate.setName(producer.getName());

            String serializedProducer = objectMapper.writeValueAsString(producerToUpdate);

            // when
            mockMvc.perform(put(UPDATE_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedProducer))
                    .andExpect(status().isOk());

            // then
            assertThat(producerRepository.findById(producerToUpdate.getName())).isPresent();
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if producer with given name does not exist")
        void shouldReturnBadRequestStatusIfProducerWithGivenNameDoesNotExist() {
            // given
            String producerName = utils.generate(String.class);

            // when
            mockMvc.perform(delete(String.format(DELETE_PATH, producerName))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete existing producer")
        void shouldDeleteExistingProducer() {
            // given
            ProducerEntity existingProducer = producerRepository.save(utils.generate(ProducerEntity.class));

            String producerName = existingProducer.getName();

            // when
            mockMvc.perform(delete(String.format(DELETE_PATH, producerName))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            assertThat(producerRepository.findById(producerName)).isEmpty();
        }
    }
}
