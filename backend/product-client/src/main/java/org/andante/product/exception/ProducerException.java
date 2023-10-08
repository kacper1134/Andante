package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProducerException extends RuntimeException {

    public ProducerException(String message) {
        super(message);
    }
}
