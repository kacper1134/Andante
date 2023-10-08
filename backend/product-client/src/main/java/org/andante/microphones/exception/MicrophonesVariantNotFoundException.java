package org.andante.microphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MicrophonesVariantNotFoundException extends MicrophonesException {

    public MicrophonesVariantNotFoundException(String message) {
        super(message);
    }
}
