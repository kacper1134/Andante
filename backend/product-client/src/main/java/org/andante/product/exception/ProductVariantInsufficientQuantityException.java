package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductVariantInsufficientQuantityException extends ProductException {

    public ProductVariantInsufficientQuantityException(String message) {
        super(message);
    }
}
