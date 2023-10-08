package org.andante.product.logic.service.impl;


import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.exception.ProducerConflictException;
import org.andante.product.exception.ProducerNotFoundException;
import org.andante.product.logic.mapper.ProducerModelEntityMapper;
import org.andante.product.logic.mapper.ProductModelEntityMapper;
import org.andante.product.logic.model.Producer;
import org.andante.product.logic.model.ProductOutput;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.ProducerEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(AmplifiersTestUtils.class)
@Transactional
public class DefaultProducerServiceTest {

    @Autowired
    private AmplifiersTestUtils amplifiersTestUtils;

    @Autowired
    private DefaultProducerService service;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private ProductModelEntityMapper productModelEntityMapper;

    @Autowired
    private ProducerModelEntityMapper mapper;

    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnEmptySetForEmptyDatabase() {
            // given
            Set<String> identifiers = amplifiersTestUtils.generate(String.class, 5);

            // when
            Set<Producer> serviceResponse = service.getAllById(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing producers")
        void shouldReturnAllExistingProducers() {
            // given
            Set<ProducerEntity> producersToPersist = amplifiersTestUtils.generate(ProducerEntity.class, 5);

            List<ProducerEntity> persistedProducers = producerRepository.saveAll(producersToPersist);

            Set<Producer> expectedResult = persistedProducers.stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            Set<String> producerNames = persistedProducers.stream()
                    .map(ProducerEntity::getName)
                    .collect(Collectors.toSet());

            producerNames.add(amplifiersTestUtils.generate(String.class));

            // when
            Set<Producer> serviceResponse = service.getAllById(producerNames);

            // then
            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get All Products")
    class GetAllProductsTests {

        @Test
        @DisplayName("should return empty set if producer has no products")
        void shouldReturnEmptySetIfProducerHasNoProducts() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            // when
            Set<ProductOutput> serviceResponse = service.getAllProducts(producer.getName());

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return empty set if producer does not exist")
        void shouldReturnEmptySetIfProducerDoesNotExist() {
            // given
            String producerName = amplifiersTestUtils.generate(String.class);

            // when
            Set<ProductOutput> serviceResponse = service.getAllProducts(producerName);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all producer's products")
        void shouldReturnAllProducerProducts() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(5);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            List<AmplifiersEntity> persistedAmplifiers = amplifiersRepository.saveAll(amplifiers);

            Set<ProductOutput> expectedResult = persistedAmplifiers.stream()
                    .map(productModelEntityMapper::toModel)
                    .collect(Collectors.toSet());

            String producerName = producer.getName();

            // when
            Set<ProductOutput> serviceResponse = service.getAllProducts(producerName);

            // then
            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should throw ProducerConflictException if producer with given identifier exists")
        void shouldThrowProducerConflictExceptionIfProducerWithGivenIdentifierExists() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Producer producerToCreate = mapper.toModel(producer);
            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(producerToCreate))
                    .isInstanceOf(ProducerConflictException.class);
        }

        @Test
        @DisplayName("should create producer if model is valid")
        void shouldCreateProducerIfModelIsValid() {
            // given
            Producer producerToCreate = amplifiersTestUtils.generate(Producer.class);

            // when
            Producer serviceResponse = service.create(producerToCreate);

            // then
            Optional<Producer> persistedProducer = producerRepository.findById(serviceResponse.getName()).map(mapper::toModel);

            assertThat(persistedProducer).isPresent();
            assertThat(persistedProducer.get()).isEqualTo(serviceResponse);
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should throw ProducerNotFoundException if producer with given identifier does not exist")
        void shouldThrowProducerConflictExceptionIfProducerWithGivenIdentifierExists() {
            // given
            Producer producerToCreate = amplifiersTestUtils.generate(Producer.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.modify(producerToCreate))
                    .isInstanceOf(ProducerNotFoundException.class);
        }

        @Test
        @DisplayName("should update producer if it exists")
        void shouldUpdateProducerIfItExists() {
            // given
            ProducerEntity persistedProducer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Producer expectedResult = mapper.toModel(persistedProducer);

            // when
            Producer serviceResponse = service.modify(expectedResult);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should throw ProducerNotFoundException if producer does not exist")
        void shouldThrowProducerNotFoundExceptionIfProducerDoesNotExist() {
            // given
            String producerName = amplifiersTestUtils.generate(String.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(producerName))
                    .isInstanceOf(ProducerNotFoundException.class)
                    .hasMessageContaining(producerName);
        }

        @Test
        @DisplayName("should delete existing producer")
        void shouldDeleteExistingProducer() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Producer expectedResult = mapper.toModel(producer);

            // when
            Producer serviceResponse = service.delete(producer.getName());

            // then
            assertThat(producerRepository.findById(producer.getName())).isEmpty();
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }
}
