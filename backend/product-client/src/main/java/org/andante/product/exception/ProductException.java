package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductException extends RuntimeException {

    public ProductException(String message) {
        super(message);
    }
}
