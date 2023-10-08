package org.andante.gramophones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GramophonesVariantConflictException extends GramophonesException {

    public GramophonesVariantConflictException(String message) {
        super(message);
    }
}
