package org.andante.headphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HeadphonesVariantConflictException extends HeadphonesException {

    public HeadphonesVariantConflictException(String message) {
        super(message);
    }
}
