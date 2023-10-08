package org.andante.product.logic.service.impl;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.product.enums.ProductViolationType;
import org.andante.product.exception.ProductVariantInsufficientQuantityException;
import org.andante.product.exception.ProductVariantNotFoundException;
import org.andante.product.logic.mapper.ProductVariantModelEntityMapper;
import org.andante.product.logic.model.ProductOrderVariant;
import org.andante.product.logic.model.ProductOrderViolation;
import org.andante.product.logic.model.ProductVariantOutput;
import org.andante.product.logic.service.ProductVariantService;
import org.andante.product.repository.ProductVariantRepository;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultProductVariantService implements ProductVariantService {

    private static final String PRODUCT_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Product variant with identifier %d does not exist";
    private static final String PRODUCT_VARIANT_INSUFFICIENT_QUANTITY_EXCEPTION_MESSAGE = "There is not enough pieces of product variant %d left(%d requested, %d remaining)";

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantModelEntityMapper productVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<ProductVariantOutput> getVariants(Set<Long> identifiers) {
        List<ProductVariantEntity> databaseResponse = productVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(productVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Optional<ProductVariantOutput> getVariant(Long identifier) {
        Optional<ProductVariantEntity> databaseResponse = productVariantRepository.findById(identifier);

        return databaseResponse.map(productVariantModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<ProductOrderViolation> validateOrder(Set<ProductOrderVariant> productOrderVariants) {
        Map<Long, Integer> variantsToQuantity = productOrderVariants.stream()
                .collect(Collectors.toMap(ProductOrderVariant::getVariantIdentifier, ProductOrderVariant::getOrderedQuantity));

        List<ProductVariantEntity> databaseResponse = productVariantRepository.findAllById(variantsToQuantity.keySet());

        Set<Long> retrievedIdentifiers = databaseResponse.stream()
                .map(ProductVariantEntity::getId)
                .collect(Collectors.toSet());

        Set<ProductOrderViolation> missingVariantViolations = Sets.difference(variantsToQuantity.keySet(), retrievedIdentifiers).stream()
                .map(this::buildMissingVariantViolation)
                .collect(Collectors.toSet());

        Set<ProductOrderViolation> insufficientQuantityViolations = databaseResponse.stream()
                .filter(productVariant -> productVariant.getAvailableQuantity() < variantsToQuantity.get(productVariant.getId()))
                .map(productVariant -> buildInsufficientVariantQuantityViolation(productVariant, variantsToQuantity.get(productVariant.getId())))
                .collect(Collectors.toSet());

        return Sets.union(missingVariantViolations, insufficientQuantityViolations);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus changeAvailableQuantity(Long identifier, Integer quantityChange) {
        Optional<ProductVariantEntity> databaseResponse = productVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new ProductVariantNotFoundException(String.format(PRODUCT_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        ProductVariantEntity productVariant = databaseResponse.get();

        if (productVariant.getAvailableQuantity() < quantityChange) {
            throw new ProductVariantInsufficientQuantityException(String.format(PRODUCT_VARIANT_INSUFFICIENT_QUANTITY_EXCEPTION_MESSAGE,
                    identifier, quantityChange, productVariant.getAvailableQuantity()));
        }

        productVariant.setAvailableQuantity(productVariant.getAvailableQuantity() - quantityChange);

        productVariantRepository.save(productVariant);

        return OperationStatus.OK;
    }

    private ProductOrderViolation buildMissingVariantViolation(Long identifier) {
        return ProductOrderViolation.builder()
                .variantIdentifier(identifier)
                .productViolationType(ProductViolationType.MISSING_PRODUCT_VARIANT)
                .message(String.format("Product variant with identifier %d does not exist", identifier))
                .build();
    }

    private ProductOrderViolation buildInsufficientVariantQuantityViolation(ProductVariantEntity productVariantEntity, Integer orderedQuantity) {
        return ProductOrderViolation.builder()
                .variantIdentifier(productVariantEntity.getId())
                .productViolationType(ProductViolationType.INSUFFICIENT_PRODUCT_VARIANT_QUANTITY)
                .message(String.format("There are only %d pieces of %s in selected variant, yet %d were ordered", productVariantEntity.getAvailableQuantity(),
                        productVariantEntity.getProduct().getName(), orderedQuantity))
                .build();
    }
}
