package org.andante.gramophones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GramophonesVariantNotFoundException extends GramophonesException {

    public GramophonesVariantNotFoundException(String message) {
        super(message);
    }
}
