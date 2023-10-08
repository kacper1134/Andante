package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProducerNotFoundException extends ProducerException {

    public ProducerNotFoundException(String message) {
        super(message);
    }
}
