package org.andante.product.logic.service;

import org.andante.enums.OperationStatus;
import org.andante.product.logic.model.ProductOrderVariant;
import org.andante.product.logic.model.ProductOrderViolation;
import org.andante.product.logic.model.ProductVariantOutput;

import java.util.Optional;
import java.util.Set;

public interface ProductVariantService {
    Set<ProductVariantOutput> getVariants(Set<Long> identifiers);
    Optional<ProductVariantOutput> getVariant(Long identifier);
    Set<ProductOrderViolation> validateOrder(Set<ProductOrderVariant> productOrderVariant);
    OperationStatus changeAvailableQuantity(Long identifier, Integer quantityChange);
}
