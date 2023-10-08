package org.andante.gramophones.logic.service.impl;

import org.andante.gramophones.configuration.GramophonesTestConfiguration;
import org.andante.gramophones.exception.GramophonesConflictException;
import org.andante.gramophones.exception.GramophonesNotFoundException;
import org.andante.gramophones.logic.mapper.GramophonesModelEntityMapper;
import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesOutput;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(GramophonesTestConfiguration.class)
@Transactional
class DefaultGramophonesServiceTest {

    @Autowired
    private GramophonesTestUtils utils;

    @Autowired
    private DefaultGramophonesService service;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private GramophonesRepository repository;

    @Autowired
    private GramophonesModelEntityMapper mapper;


    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void whenRepositoryIsEmptyEmptySetIsReturned() {
            // given
            Set<Long> sampleIdentifiers = Set.of(utils.generate(Long.class));
            // when
            Set<GramophonesOutput> serviceResponse = service.getAllById(sampleIdentifiers);
            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return only existing gramophones")
        void whenDatabaseEntryExistsItShouldBeReturned() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            Set<GramophonesEntity> gramophonesToPersist = utils.generateGramophones(5);
            gramophonesToPersist.forEach(gramophones -> gramophones.setProducer(producer));

            Set<GramophonesOutput> existingGramophones = repository.saveAll(gramophonesToPersist).stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            Set<Long> identifiers = existingGramophones.stream()
                    .map(GramophonesOutput::getId)
                    .collect(Collectors.toSet());

            identifiers.add(utils.generate(Long.class));

            // when
            Set<GramophonesOutput> serviceResponse = service.getAllById(identifiers);

            // then
            assertThat(serviceResponse)
                    .isNotNull()
                    .hasSameElementsAs(existingGramophones);
        }

    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should throw GramophonesConflictException when gramophones with given identifier exists")
        void shouldRaiseExceptionWhenGramophonesWithGivenIdExist() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophonesToGenerate = utils.generateGramophone();
            gramophonesToGenerate.setProducer(producer);
            GramophonesEntity databaseState = repository.save(gramophonesToGenerate);
            GramophonesInput gramophonesInput = utils.toInput(databaseState);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(gramophonesInput))
                    .isInstanceOf(GramophonesConflictException.class);
        }

        @Test
        @DisplayName("should correctly create instance when all criteria are met")
        void shouldCorrectlyCreateInstanceWhenAllCriteriaAreMet() {
            // given
            ProducerEntity existingProducer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(existingProducer);

            GramophonesInput gramophonesToCreate = utils.toInput(gramophones);

            // when
            GramophonesOutput createdGramophones = service.create(gramophonesToCreate);

            // then
            assertThat(createdGramophones).isNotNull();
            assertThat(createdGramophones.getId()).isPositive();
        }

    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise GramophonesNotFoundException when gramophone with given identifier does not exist")
        void shouldRaiseGramophonesNotFoundExceptionWhenEntityDoesNotExist() {
            // given
            GramophonesEntity gramophones = utils.generateGramophone();
            GramophonesInput gramophonesToUpdate = utils.toInput(gramophones);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.modify(gramophonesToUpdate))
                    .isInstanceOf(GramophonesNotFoundException.class);
        }

        @Test
        @DisplayName("should correctly update gramohpones given all other conditions are met")
        void shouldCorrectlyUpdateGramophonesWhenOtherConditionsAreMet() {
            // given
            ProducerEntity existingProducer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(existingProducer);
            GramophonesEntity existingGramophones = repository.save(gramophones);
            GramophonesInput gramophonesInput = utils.toInput(existingGramophones);

            // when
            GramophonesOutput serviceResponse = service.modify(gramophonesInput);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getId()).isPositive();
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should throw GramophonesNotFoundException when gramophones with given identifier do not exist")
        void shouldThrowGramophonesNotFoundExceptionWhenGramophonesDoNotExist() {
            // given
            Long missingIdentifier = utils.generate(Long.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(missingIdentifier))
                    .isInstanceOf(GramophonesNotFoundException.class);
        }

        @Test
        @DisplayName("should delete gramophones given all other criteria are met")
        void shouldDeleteAGramophonesGivenAllOtherCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            GramophonesEntity gramophones = utils.generateGramophone();
            gramophones.setProducer(producer);
            GramophonesEntity existingGramophones = repository.save(gramophones);

            // when
            GramophonesOutput serviceResponse = service.delete(existingGramophones.getId());

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(repository.findById(existingGramophones.getId())).isEmpty();
        }
    }

}
