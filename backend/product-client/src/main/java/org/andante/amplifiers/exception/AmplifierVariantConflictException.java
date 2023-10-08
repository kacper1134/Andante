package org.andante.amplifiers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AmplifierVariantConflictException extends AmplifierException {

    public AmplifierVariantConflictException(String message) {
        super(message);
    }
}
