package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProducerConflictException extends ProducerException {

    public ProducerConflictException(String message) {
        super(message);
    }
}
