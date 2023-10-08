package org.andante.microphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MicrophonesConflictException extends MicrophonesException {

    public MicrophonesConflictException(String message) {
        super(message);
    }
}
