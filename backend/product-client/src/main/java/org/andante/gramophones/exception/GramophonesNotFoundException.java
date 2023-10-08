package org.andante.gramophones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GramophonesNotFoundException extends GramophonesException {

    public GramophonesNotFoundException(String message) {
        super(message);
    }
}
