package org.andante.gramophones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GramophonesConflictException extends GramophonesException {

    public GramophonesConflictException(String message) {
        super(message);
    }
}
