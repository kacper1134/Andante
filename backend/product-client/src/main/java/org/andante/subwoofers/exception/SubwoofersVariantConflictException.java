package org.andante.subwoofers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubwoofersVariantConflictException extends SubwoofersException {

    public SubwoofersVariantConflictException(String message) {
        super(message);
    }
}
