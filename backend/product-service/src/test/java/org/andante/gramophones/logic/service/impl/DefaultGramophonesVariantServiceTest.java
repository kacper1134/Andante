package org.andante.gramophones.logic.service.impl;

import org.andante.gramophones.exception.GramophonesVariantConflictException;
import org.andante.gramophones.exception.GramophonesVariantNotFoundException;
import org.andante.gramophones.logic.mapper.GramophonesVariantModelEntityMapper;
import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;
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
@Import(GramophonesTestUtils.class)
@Transactional
public class DefaultGramophonesVariantServiceTest {

    @Autowired
    private GramophonesTestUtils utils;

    @Autowired
    private DefaultGramophonesVariantService service;

    @Autowired
    private GramophonesRepository gramophonesRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private GramophonesVariantRepository repository;

    @Autowired
    private GramophonesVariantModelEntityMapper mapper;

    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnEmptySetForEmptyDatabase() {
            // given
            Set<Long> requestedIdentifiers = utils.generate(Long.class, 5);

            // when
            Set<GramophonesVariantOutput> serviceResponse = service.getAllByIds(requestedIdentifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing gramophones variants")
        void shouldReturnAllExistingGramophonesVariants() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesEntity existingGramophones = gramophonesRepository.save(gramophones);
            Set<GramophonesVariantEntity> gramophonesVariants = utils.generate(GramophonesVariantEntity.class, 5);
            gramophonesVariants.forEach(variant -> variant.setGramophones(existingGramophones));
            List<GramophonesVariantEntity> existingVariants = repository.saveAll(gramophonesVariants);
            Set<Long> identifiers = existingVariants.stream()
                    .map(GramophonesVariantEntity::getId)
                    .collect(Collectors.toSet());

            identifiers.addAll(utils.generate(Long.class, 5));

            // when
            Set<GramophonesVariantOutput> serviceResponse = service.getAllByIds(identifiers);

            Set<GramophonesVariantOutput> expectedResult = existingVariants.stream()
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
            Set<GramophonesVariantOutput> serviceResponse = service.getAllByGramophoneId(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all product variants")
        void shouldReturnAllProductVariants() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesEntity generatedGramophones = gramophonesRepository.save(gramophones);
            Set<GramophonesVariantEntity> gramophonesVariants = utils.generate(GramophonesVariantEntity.class, 5);
            gramophonesVariants.forEach(variant -> variant.setGramophones(generatedGramophones));
            List<GramophonesVariantEntity> persistedVariants = repository.saveAll(gramophonesVariants);

            Long identifier = generatedGramophones.getId();

            // when
            Set<GramophonesVariantOutput> serviceResponse = service.getAllByGramophoneId(identifier);

            Set<GramophonesVariantOutput> expectedResult = persistedVariants.stream()
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
        @DisplayName("should raise GramophonesVariantConflictException when variant with given identifier already exists")
        void shouldRaiseDomainExceptionWhenVariantWithGivenIdentifierAlreadyExists() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesEntity generatedGramophones = gramophonesRepository.save(gramophones);
            GramophonesVariantEntity variantToPersist = utils.generate(GramophonesVariantEntity.class);
            variantToPersist.setGramophones(generatedGramophones);
            GramophonesVariantEntity persistedVariant = repository.save(variantToPersist);

            GramophonesVariantInput gramophonesVariantInput = utils.toInput(persistedVariant);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(gramophonesVariantInput))
                    .isInstanceOf(GramophonesVariantConflictException.class);
        }

        @Test
        @DisplayName("should create variant given all criteria are met")
        void shouldCreateVariantGivenAllCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesEntity generatedGramophones = gramophonesRepository.save(gramophones);

            GramophonesVariantEntity variantToCreate = utils.generate(GramophonesVariantEntity.class);
            variantToCreate.setGramophones(generatedGramophones);

            GramophonesVariantInput gramophonesVariantInput = utils.toInput(variantToCreate);

            // when
            GramophonesVariantOutput serviceResponse = service.create(gramophonesVariantInput);

            // then
            assertThat(serviceResponse).isNotNull();
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise GramophonesVariantNotFoundException when variant with given identifier does not exist")
        void shouldRaiseGramophonesVariantNotFoundExceptionWhenVariantWithGivenIdentifierDoesNotExist() {
            // given
            GramophonesVariantInput gramophonesVariantInput = utils.generate(GramophonesVariantInput.class);

            // when
            assertThat(repository.findById(gramophonesVariantInput.getId())).isEmpty();

            // then
            assertThatThrownBy(() -> service.modify(gramophonesVariantInput))
                    .isInstanceOf(GramophonesVariantNotFoundException.class);
        }

        @Test
        @DisplayName("should update gramophone variant when all other criteria are met")
        void shouldUpdateGramophonesVariantWhenAllOtherCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophonesToPersist = utils.generateGramophone();

            gramophonesToPersist.setProducer(producer);
            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophonesToPersist);

            GramophonesVariantEntity gramophonesVariantEntity = utils.generate(GramophonesVariantEntity.class);
            gramophonesVariantEntity.setGramophones(persistedGramophones);

            GramophonesVariantEntity persistedVariant = repository.save(gramophonesVariantEntity);

            GramophonesVariantInput gramophonesVariantInput = utils.toInput(persistedVariant);

            // when
            GramophonesVariantOutput serviceResponse = service.modify(gramophonesVariantInput);

            GramophonesVariantOutput expectedResult = mapper.toModel(persistedVariant);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should raise GramophonesVariantNotFoundException when variant with given identifier is missing")
        void shouldRaiseGramophonesVariantNotFoundExceptionWhenVariantWithGivenIdentifierIsMissing() {
            // given
            Long identifier = utils.generate(Long.class);

            // when
            assertThat(repository.findById(identifier)).isEmpty();

            // then
            assertThatThrownBy(() -> service.delete(identifier))
                    .isInstanceOf(GramophonesVariantNotFoundException.class);
        }

        @Test
        @DisplayName("should delete existing variant")
        void shouldDeleteExistingVariant() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophonesToPersist = utils.generateGramophone();
            gramophonesToPersist.setProducer(producer);

            GramophonesEntity persistedGramophones = gramophonesRepository.save(gramophonesToPersist);
            GramophonesVariantEntity variantToPersist = utils.generate(GramophonesVariantEntity.class);
            variantToPersist.setGramophones(persistedGramophones);

            GramophonesVariantEntity persistedVariant = repository.save(variantToPersist);

            Long identifier = persistedVariant.getId();

            // when
            GramophonesVariantOutput serviceResponse = service.delete(identifier);

            GramophonesVariantOutput expectedResponse = mapper.toModel(persistedVariant);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResponse);
            assertThat(repository.findById(persistedVariant.getId())).isEmpty();
        }
    }
}
