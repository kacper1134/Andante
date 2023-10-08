package org.andante.amplifiers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AmplifiersConflictException extends AmplifierException {

    public AmplifiersConflictException(String message) {
        super(message);
    }
}
