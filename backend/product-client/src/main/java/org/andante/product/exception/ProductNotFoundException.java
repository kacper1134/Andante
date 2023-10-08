package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
