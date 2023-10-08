package org.andante.product.logic.service.impl;

import org.andante.amplifiers.logic.mapper.AmplifiersVariantModelEntityMapper;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.AmplifiersVariantRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.enums.ProductViolationType;
import org.andante.product.exception.ProductVariantInsufficientQuantityException;
import org.andante.product.exception.ProductVariantNotFoundException;
import org.andante.product.logic.model.ProductOrderVariant;
import org.andante.product.logic.model.ProductOrderViolation;
import org.andante.product.logic.model.ProductVariantOutput;
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
public class DefaultProductVariantServiceTest {

    @Autowired
    private AmplifiersTestUtils amplifiersTestUtils;

    @Autowired
    private DefaultProductVariantService service;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private AmplifiersVariantRepository amplifiersVariantRepository;

    @Autowired
    private AmplifiersVariantModelEntityMapper amplifiersVariantModelEntityMapper;

    @Nested
    @DisplayName("Get Variants")
    class GetVariantsClass {

        @Test
        @DisplayName("should return empty set if database is empty")
        void shouldReturnEmptySetIfDatabaseIsEmpty() {
            // given
            Set<Long> identifiers = Set.of(1L, 2L, 3L);

            // when
            Set<ProductVariantOutput> serviceResponse = service.getVariants(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing variants")
        void shouldReturnAllExistingVariants() {
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

            Set<AmplifiersVariantOutput> expectedResult = persistedVariants.stream()
                    .map(amplifiersVariantModelEntityMapper::toModel)
                    .collect(Collectors.toSet());

            // when
            Set<ProductVariantOutput> serviceResponse = service.getVariants(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get Variant")
    class GetVariantTests {

        @Test
        @DisplayName("should return empty optional if variant does not exist")
        void shouldReturnEmptyOptionalIfVariantDoesNotExist() {
            // given
            Long identifier = 1L;

            // when
            Optional<ProductVariantOutput> serviceResponse = service.getVariant(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return variant when it exists")
        void shouldReturnVariantWhenItExists() {
            // given
            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity variant = amplifiersTestUtils.generate(AmplifiersVariantEntity.class);
            variant.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = amplifiersVariantRepository.save(variant);

            AmplifiersVariantOutput expectedResult = amplifiersVariantModelEntityMapper.toModel(persistedVariant);

            // when
            Optional<ProductVariantOutput> serviceResponse = service.getVariant(persistedVariant.getId());

            // then
            assertThat(serviceResponse).isNotNull().isNotEmpty();
            assertThat(serviceResponse.get()).isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Validate Order")
    class ValidateOrderTests {

        @Test
        @DisplayName("should return missing product violations when variants do not exist")
        void shouldReturnMissingProductViolationsWhenVariantsDoNotExist() {
            // given
            Set<ProductOrderVariant> orderVariants = Set.of(ProductOrderVariant.builder()
                    .variantIdentifier(1L)
                    .orderedQuantity(5)
                    .build());

            // when
            Set<ProductOrderViolation> violations = service.validateOrder(orderVariants);

            // then
            assertThat(violations).isNotNull().hasSize(1)
                    .allMatch(violation -> violation.getProductViolationType().equals(ProductViolationType.MISSING_PRODUCT_VARIANT));
        }

        @Test
        @DisplayName("should return insufficient quantity violations when there are not enough products")
        void shouldReturnInsufficientQuantityViolationsWhenThereAreNotEnoughProducts() {
            // given
            Integer availableQuantity = 5;
            Integer requestedQuantity = 10;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity amplifiersVariantEntity = amplifiersTestUtils.generate(AmplifiersVariantEntity.class);
            amplifiersVariantEntity.setAmplifiers(persistedAmplifiers);
            amplifiersVariantEntity.setAvailableQuantity(availableQuantity);

            AmplifiersVariantEntity persistedVariant = amplifiersVariantRepository.save(amplifiersVariantEntity);

            ProductOrderVariant productOrderVariant = ProductOrderVariant.builder()
                    .variantIdentifier(persistedVariant.getId())
                    .orderedQuantity(requestedQuantity)
                    .build();

            // when
            Set<ProductOrderViolation> orderViolations = service.validateOrder(Set.of(productOrderVariant));

            // then
            assertThat(orderViolations).isNotNull().hasSize(1)
                    .allMatch(violation -> violation.getProductViolationType().equals(ProductViolationType.INSUFFICIENT_PRODUCT_VARIANT_QUANTITY));
        }
    }

    @Nested
    @DisplayName("Change Available Quantity")
    class ChangeAvailableQuantityTests {

        @Test
        @DisplayName("should raise ProductVariantNotFoundException if it does not exist")
        void shouldRaiseProductVariantNotFoundExceptionIfItDoesNotExist() {
            // given
            Long identifier = 1L;
            Integer quantityChange = 5;

            // when
            assertThat(amplifiersVariantRepository.findById(identifier)).isEmpty();

            // then
            assertThatThrownBy(() -> service.changeAvailableQuantity(identifier, quantityChange))
                    .isInstanceOf(ProductVariantNotFoundException.class)
                    .hasMessageContaining(String.valueOf(identifier));
        }

        @Test
        @DisplayName("should raise ProductVariantInsufficientQuantityException if quantity is not sufficient")
        void shouldRaiseProductVariantInsufficientQuantityExceptionIfQuantityIsNotSufficient() {
            // given
            Integer existingQuantity = 5;
            Integer requestedQuantity = 10;

            ProducerEntity producer = producerRepository.save(amplifiersTestUtils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = amplifiersTestUtils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            AmplifiersVariantEntity amplifiersVariantEntity = amplifiersTestUtils.generate(AmplifiersVariantEntity.class);
            amplifiersVariantEntity.setAvailableQuantity(existingQuantity);
            amplifiersVariantEntity.setAmplifiers(persistedAmplifiers);

            AmplifiersVariantEntity persistedVariant = amplifiersVariantRepository.save(amplifiersVariantEntity);

            Long identifier = persistedVariant.getId();

            // when
            // always

            // then
            assertThatThrownBy(() -> service.changeAvailableQuantity(identifier, requestedQuantity))
                    .isInstanceOf(ProductVariantInsufficientQuantityException.class)
                    .hasMessageContaining(String.valueOf(identifier));
        }
    }
}
