package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductVariantNotFoundException extends ProductException {

    public ProductVariantNotFoundException(String message) {
        super(message);
    }
}
