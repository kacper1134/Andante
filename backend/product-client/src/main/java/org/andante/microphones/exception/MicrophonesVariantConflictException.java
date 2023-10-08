package org.andante.microphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MicrophonesVariantConflictException extends MicrophonesException {

    public MicrophonesVariantConflictException(String message) {
        super(message);
    }
}
