package org.andante.product.logic.service.impl;

import org.andante.amplifiers.logic.mapper.AmplifiersModelEntityMapper;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.enums.OperationStatus;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.enums.ProductSortingOrder;
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
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(AmplifiersTestUtils.class)
@Transactional
public class DefaultProductServiceTest {

    @Autowired
    private AmplifiersTestUtils amplifiersTestUtils;

    @Autowired
    private DefaultProductService service;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private AmplifiersModelEntityMapper amplifiersModelEntityMapper;

    @Nested
    @DisplayName("Get Products")
    class GetProductsTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnEmptySetForEmptyDatabase() {
            // given
            Set<Long> identifiers = amplifiersTestUtils.generate(Long.class, 10);

            // when
            Set<ProductOutput> serviceResponse = service.getProducts(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing products")
        void shouldReturnAllExistingProducts() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(5);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            List<AmplifiersEntity> persistedAmplifiers = amplifiersRepository.saveAll(amplifiers);

            Set<AmplifiersOutput> expectedResult = persistedAmplifiers.stream()
                    .map(amplifiersModelEntityMapper::toModel)
                    .collect(Collectors.toSet());

            Set<Long> identifiers = persistedAmplifiers.stream()
                    .map(AmplifiersEntity::getId)
                    .collect(Collectors.toSet());

            identifiers.addAll(amplifiersTestUtils.generate(Long.class, 5));

            // when
            Set<ProductOutput> serviceResponse = service.getProducts(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get By Query")
    class GetByQueryTests {

        @Test
        @DisplayName("should return no elements when page size is bigger than total amount of pages")
        void shouldReturnNoElementsWhenPageSizeIsBiggerThanTotalAmountOfPages() {
            // given
            int totalElements = 5;
            int pageNumber = 3;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(totalElements);

            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));
            amplifiersRepository.saveAll(amplifiers);

            ProductQuerySpecification querySpecification = ProductQuerySpecification.builder()
                    .query("id=gt=0")
                    .pageNumber(pageNumber)
                    .pageSize(totalElements)
                    .sortingOrder(ProductSortingOrder.ALPHABETICAL)
                    .build();

            // when
            Page<ProductOutput> serviceResponse = service.getByQuery(querySpecification, 0.0);

            // then
            assertThat(serviceResponse).isNotNull();
        }

        @Test
        @DisplayName("should return all elements when page size is bigger than total amount of existing products")
        void shouldReturnAllElementsWhenPageSizeIsBiggerThanTotalAmountOfExistingProducts() {
            // given
            int totalElements = 5;
            int pageSize = 6;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(totalElements);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            amplifiersRepository.saveAll(amplifiers);

            ProductQuerySpecification querySpecification = ProductQuerySpecification.builder()
                    .query("id=gt=0")
                    .pageNumber(0)
                    .pageSize(pageSize)
                    .sortingOrder(ProductSortingOrder.ALPHABETICAL)
                    .build();

            // when
            Page<ProductOutput> serviceResponse = service.getByQuery(querySpecification, 0.0);

            // then
            assertThat(serviceResponse).isNotNull();
        }

        @Test
        @DisplayName("should return elements sorted according to specified sorting order")
        void shouldReturnElementsSortedAccordingToSpecifiedSortingOrder() {
            // given
            int totalElements = 5;
            int pageNumber = 1;
            int pageSize = 3;
            ProductSortingOrder sortingOrder = ProductSortingOrder.PRICE_DESCENDING;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            Set<AmplifiersEntity> amplifiers = amplifiersTestUtils.generateAmplifiers(totalElements);
            amplifiers.forEach(amplifier -> amplifier.setProducer(producer));

            amplifiersRepository.saveAll(amplifiers);

            ProductQuerySpecification querySpecification = ProductQuerySpecification.builder()
                    .query("id=gt=0")
                    .pageNumber(pageNumber)
                    .pageSize(pageSize)
                    .sortingOrder(sortingOrder)
                    .build();

            // when
            Page<ProductOutput> serviceResponse = service.getByQuery(querySpecification, 0.0);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getContent()).isSortedAccordingTo((p1, p2) -> p1.getBasePrice().subtract(p2.getBasePrice()).intValue());
        }
    }

    @Nested
    @DisplayName("Change Observation Status")
    class ChangeObservationStatusTests {

        @Test
        @DisplayName("should return not found status if product with given identifier does not exist")
        void shouldReturnNotFoundStatusIfProductWithGivenIdentifierDoesNotExist() {
            // given
            Long identifier = amplifiersTestUtils.generate(Long.class);
            String observer = "test@gmail.com";

            // when
            OperationStatus serviceResponse = service.changeObservationStatus(observer, identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(OperationStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("should add provided user to observers if he is not already one")
        void shouldAddProvidedUserToObserversIfHeIsNotAlreadyOne() {
            // given
            String observer = "test@gmail.com";

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);
            Long identifier = persistedAmplifiers.getId();

            assertThat(persistedAmplifiers.getObservers()).doesNotContain(observer);

            // when
            OperationStatus serviceResponse = service.changeObservationStatus(observer, identifier);

            // then
            Optional<AmplifiersEntity> updatedAmplifiers = amplifiersRepository.findById(identifier);

            assertThat(updatedAmplifiers).isPresent();
            assertThat(updatedAmplifiers.get().getObservers()).contains(observer);
            assertThat(serviceResponse).isNotNull().isEqualTo(OperationStatus.OK);
        }

        @Test
        @DisplayName("should remove provided user from observers if he is already one")
        void shouldRemoveProvidedUserFromObserversIfHeIsAlreadyOne() {
            // given
            String observer = "test@gmail.com";

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);
            amplifiers.getObservers().add(observer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);
            Long identifier = persistedAmplifiers.getId();

            assertThat(persistedAmplifiers.getObservers()).contains(observer);

            // when
            OperationStatus serviceResponse = service.changeObservationStatus(observer, identifier);

            // then
            Optional<AmplifiersEntity> updatedAmplifiers = amplifiersRepository.findById(identifier);

            assertThat(updatedAmplifiers).isPresent();
            assertThat(updatedAmplifiers.get().getObservers()).doesNotContain(observer);
            assertThat(serviceResponse).isNotNull().isEqualTo(OperationStatus.OK);
        }
    }
}
