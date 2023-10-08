package org.andante.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RSQLException extends RuntimeException {

    public RSQLException(String message) {
        super(message);
    }
}
