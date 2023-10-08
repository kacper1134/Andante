package org.andante.gramophones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GramophonesException extends RuntimeException {

    public GramophonesException(String message) {
        super(message);
    }
}
