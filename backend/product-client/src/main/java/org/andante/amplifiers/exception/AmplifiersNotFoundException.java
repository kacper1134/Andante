package org.andante.amplifiers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AmplifiersNotFoundException extends AmplifierException {

    public AmplifiersNotFoundException(String message) {
        super(message);
    }
}
