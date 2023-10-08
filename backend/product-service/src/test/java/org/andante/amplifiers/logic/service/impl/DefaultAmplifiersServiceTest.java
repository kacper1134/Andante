package org.andante.amplifiers.logic.service.impl;

import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.exception.AmplifiersConflictException;
import org.andante.amplifiers.exception.AmplifiersNotFoundException;
import org.andante.amplifiers.logic.mapper.AmplifiersModelEntityMapper;
import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
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

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(AmplifiersTestConfiguration.class)
@Transactional
class DefaultAmplifiersServiceTest {

    @Autowired
    private AmplifiersTestUtils utils;

    @Autowired
    private DefaultAmplifiersService service;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersRepository repository;

    @Autowired
    private AmplifiersModelEntityMapper mapper;


    @Nested
    @DisplayName("Get All By Id")
    class GetAllByIdTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void whenRepositoryIsEmptyEmptySetIsReturned() {
            // given
            Set<Long> sampleIdentifiers = Set.of(1L, 2L, 3L);
            // when
            Set<AmplifiersOutput> serviceResponse = service.getAllById(sampleIdentifiers);
            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return only existing amplifiers")
        void whenDatabaseEntryExistsItShouldBeReturned() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiersToPersist = utils.generateAmplifiers(5);
            amplifiersToPersist.forEach(amplifier -> amplifier.setProducer(producer));

            Set<AmplifiersOutput> existingAmplifiers = repository.saveAll(amplifiersToPersist).stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            Set<Long> identifiers = existingAmplifiers.stream()
                    .map(AmplifiersOutput::getId)
                    .collect(Collectors.toSet());

            identifiers.add(utils.generate(Long.class));

            // when
            Set<AmplifiersOutput> serviceResponse = service.getAllById(identifiers);

            // then
            assertThat(serviceResponse)
                    .isNotNull()
                    .hasSameElementsAs(existingAmplifiers);
        }

    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should throw AmplifiersConflictException when amplifiers with given identifier exists")
        void shouldRaiseExceptionWhenAmplifiersWithGivenIdExist() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiersToGenerate = utils.generateAmplifier();
            amplifiersToGenerate.setProducer(producer);
            AmplifiersEntity databaseState = repository.save(amplifiersToGenerate);
            AmplifiersInput amplifiersInput = utils.toInput(databaseState);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(amplifiersInput))
                    .isInstanceOf(AmplifiersConflictException.class);
        }

        @Test
        @DisplayName("should correctly create instance when all criteria are met")
        void shouldCorrectlyCreateInstanceWhenAllCriteriaAreMet() {
            // given
            ProducerEntity existingProducer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(existingProducer);

            AmplifiersInput amplifiersToCreate = utils.toInput(amplifiers);

            // when
            AmplifiersOutput createdAmplifiers = service.create(amplifiersToCreate);

            // then
            assertThat(createdAmplifiers).isNotNull();
            assertThat(createdAmplifiers.getId()).isPositive();
        }

    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise AmplifiersNotFoundException when amplifier with given identifier does not exist")
        void shouldRaiseAmplifiersNotFoundExceptionWhenEntityDoesNotExist() {
            // given
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            AmplifiersInput amplifiersToUpdate = utils.toInput(amplifiers);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.update(amplifiersToUpdate))
                    .isInstanceOf(AmplifiersNotFoundException.class);
        }

        @Test
        @DisplayName("should correctly update amplifiers given all other conditions are met")
        void shouldCorrectlyUpdateAmplifiersWhenOtherConditionsAreMet() {
            // given
            ProducerEntity existingProducer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(existingProducer);
            AmplifiersEntity existingAmplifiers = repository.save(amplifiers);
            AmplifiersInput amplifiersInput = utils.toInput(existingAmplifiers);

            // when
            AmplifiersOutput serviceResponse = service.update(amplifiersInput);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getId()).isPositive();
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should throw AmplifiersNotFoundException when amplifiers with given identifier do not exist")
        void shouldThrowAmplifiersNotFoundExceptionWhenAmplifiersDoNotExist() {
            // given
            Long missingIdentifier = utils.generate(Long.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(missingIdentifier))
                    .isInstanceOf(AmplifiersNotFoundException.class);
        }

        @Test
        @DisplayName("should delete Amplifiers given all other criteria are met")
        void shouldDeleteAmplifiersGivenAllOtherCriteriaAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));
            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);
            AmplifiersEntity existingAmplifier = repository.save(amplifiers);

            // when
            AmplifiersOutput serviceResponse = service.delete(existingAmplifier.getId());

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(repository.findById(existingAmplifier.getId())).isEmpty();
        }
    }

}
