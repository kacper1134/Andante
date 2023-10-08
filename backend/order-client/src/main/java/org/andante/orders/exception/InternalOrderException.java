package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InternalOrderException extends RuntimeException{

    public InternalOrderException(String message) {
        super(message);
    }
}
