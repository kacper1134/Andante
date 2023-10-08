package org.andante.subwoofers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubwoofersVariantNotFoundException extends SubwoofersException {

    public SubwoofersVariantNotFoundException(String message) {
        super(message);
    }
}
