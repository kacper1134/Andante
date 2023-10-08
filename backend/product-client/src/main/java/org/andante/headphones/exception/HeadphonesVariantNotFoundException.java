package org.andante.headphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HeadphonesVariantNotFoundException extends HeadphonesException {

    public HeadphonesVariantNotFoundException(String message) {
        super(message);
    }
}
