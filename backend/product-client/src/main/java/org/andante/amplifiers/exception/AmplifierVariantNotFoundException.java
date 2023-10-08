package org.andante.amplifiers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AmplifierVariantNotFoundException extends AmplifierException {

    public AmplifierVariantNotFoundException(String message) {
        super(message);
    }
}
