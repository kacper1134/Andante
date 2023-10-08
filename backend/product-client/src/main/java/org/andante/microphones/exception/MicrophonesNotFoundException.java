package org.andante.microphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MicrophonesNotFoundException extends MicrophonesException {

    public MicrophonesNotFoundException(String message) {
        super(message);
    }
}
