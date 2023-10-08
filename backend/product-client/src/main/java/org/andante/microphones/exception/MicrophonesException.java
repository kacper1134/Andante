package org.andante.microphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MicrophonesException extends RuntimeException {

    public MicrophonesException(String message) {
        super(message);
    }
}
