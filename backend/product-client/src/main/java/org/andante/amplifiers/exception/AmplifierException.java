package org.andante.amplifiers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AmplifierException extends RuntimeException {

    public AmplifierException(String message) {
        super(message);
    }
}
