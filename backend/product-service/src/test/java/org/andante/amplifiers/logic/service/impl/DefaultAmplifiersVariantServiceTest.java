package org.andante.amplifiers.logic.service.impl;

import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.exception.AmplifierVariantConflictException;
import org.andante.amplifiers.exception.AmplifierVariantNotFoundException;
import org.andante.amplifiers.logic.mapper.AmplifiersVariantModelEntityMapper;
import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(AmplifiersTestConfiguration.class)
@Transactional
public class DefaultAmplifiersVariantServiceTest {

    @Autowired
    private AmplifiersTestUtils utils;

    @Autowired
    private DefaultAmplifiersVariantService service;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersVariantRepository repository;

    @Autowired
    private AmplifiersVariantModelEntityMapper mapper;

    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnEmptySetForEmptyDatabase() {
            // given
            Set<Long> requestedIdentifiers = utils.generate(Long.class, 5);

            // when
            Set<AmplifiersVariantOutput> serviceResponse = service.getAllById(requestedIdentifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing amplifiers variants")
        void shouldReturnAllExistingAmplifiersVariants() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity existingAmplifiers = amplifiersRepository.save(amplifiers);
            Set<AmplifiersVariantEntity> amplifiersVariants = utils.generate(AmplifiersVariantEntity.class, 5);
            amplifiersVariants.forEach(variant -> variant.setAmplifiers(existingAmplifiers));
            List<AmplifiersVariantEntity> existingVariants = repository.saveAll(amplifiersVariants);
            Set<Long> identifiers = existingVariants.stream()
                    .map(AmplifiersVariantEntity::getId)
                    .collect(Collectors.toSet());

            identifiers.addAll(utils.generate(Long.class, 5));

            // when
            Set<AmplifiersVariantOutput> serviceResponse = service.getAllById(identifiers);

            Set<AmplifiersVariantOutput> expectedResult = existingVariants.stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get All By Product Id")
    class GetAllByProductId {

        @Test
        @DisplayName("should return empty set for non existing product")
        void shouldReturnEmptySetForNonExistingProduct() {
            // given
            Long identifier = 1L;

            // when
            Set<AmplifiersVariantOutput> serviceResponse = service.getAllByProductId(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all product variants")
        void shouldReturnAllProductVariants() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity generatedAmplifiers = amplifiersRepository.save(amplifiers);
            Set<AmplifiersVariantEntity> amplifierVariants = utils.generate(AmplifiersVariantEntity.class, 5);
            amplifierVariants.forEach(variant -> variant.setAmplifiers(generatedAmplifiers));
            List<AmplifiersVariantEntity> persistedAmplifierVariants = repository.saveAll(amplifierVariants);

            Long identifier = generatedAmplifiers.getId();

            // when
            Set<AmplifiersVariantOutput> serviceResponse = service.getAllByProductId(identifier);

            Set<AmplifiersVariantOutput> expectedResult = persistedAmplifierVariants.stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            // then
            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should raise AmplifierVariantConflictException when variant with given identifier already exists")
        void shouldRaiseDomainExceptionWhenVariantWithGivenIdentifierAlreadyExists() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity generatedAmplifiers = amplifiersRepository.save(amplifiers);
            AmplifiersVariantEntity variantToPersist = utils.generate(AmplifiersVariantEntity.class);
            variantToPersist.setAmplifiers(generatedAmplifiers);
            AmplifiersVariantEntity persistedVariant = repository.save(variantToPersist);

            AmplifiersVariantInput amplifiersVariantInput = utils.toInput(persistedVariant);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(amplifiersVariantInput))
                    .isInstanceOf(AmplifierVariantConflictException.class);
        }

        @Test
        @DisplayName("should create variant given all criteria are met")
        void shouldCreateVariantGivenAllCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity generatedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity variantToCreate = utils.generate(AmplifiersVariantEntity.class);
            variantToCreate.setAmplifiers(generatedAmplifiers);

            AmplifiersVariantInput amplifiersVariantInput = utils.toInput(variantToCreate);

            // when
            AmplifiersVariantOutput serviceResponse = service.create(amplifiersVariantInput);

            // then
            assertThat(serviceResponse).isNotNull();
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise AmplifierVariantNotFoundException when variant with given identifier does not exist")
        void shouldRaiseAmplifierVariantNotFoundExceptionWhenVariantWithGivenIdentifierDoesNotExist() {
            // given
            AmplifiersVariantInput amplifiersVariantInput = utils.generate(AmplifiersVariantInput.class);

            // when
            assertThat(repository.findById(amplifiersVariantInput.getId())).isEmpty();

            // then
            assertThatThrownBy(() -> service.update(amplifiersVariantInput))
                    .isInstanceOf(AmplifierVariantNotFoundException.class);
        }

        @Test
        @DisplayName("should update amplifier variant when all other criteria are met")
        void shouldUpdateAmplifierVariantWhenAllOtherCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiersToPersist = utils.generateAmplifier();

            amplifiersToPersist.setProducer(producer);
            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiersToPersist);

            AmplifiersVariantEntity amplifiersVariantEntity = utils.generate(AmplifiersVariantEntity.class);
            amplifiersVariantEntity.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = repository.save(amplifiersVariantEntity);

            AmplifiersVariantInput amplifiersVariantInput = utils.toInput(persistedVariant);

            // when
            AmplifiersVariantOutput serviceResponse = service.update(amplifiersVariantInput);

            AmplifiersVariantOutput expectedResult = mapper.toModel(persistedVariant);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should raise AmplifiersVariantNotFoundException when variant with given identifier is missing")
        void shouldRaiseAmplifiersVariantNotFoundExceptionWhenVariantWithGivenIdentifierIsMissing() {
            // given
            Long identifier = 1L;

            // when
            assertThat(repository.findById(identifier)).isEmpty();

            // then
            assertThatThrownBy(() -> service.delete(identifier))
                    .isInstanceOf(AmplifierVariantNotFoundException.class);
        }

        @Test
        @DisplayName("should delete existing variant")
        void shouldDeleteExistingVariant() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiersToPersist = utils.generateAmplifier();
            amplifiersToPersist.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiersToPersist);
            AmplifiersVariantEntity variantToPersist = utils.generate(AmplifiersVariantEntity.class);
            variantToPersist.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = repository.save(variantToPersist);

            Long identifier = persistedVariant.getId();

            // when
            AmplifiersVariantOutput serviceResponse = service.delete(identifier);

            AmplifiersVariantOutput expectedResponse = mapper.toModel(persistedVariant);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResponse);
            assertThat(repository.findById(persistedVariant.getId())).isEmpty();
        }
    }
}
