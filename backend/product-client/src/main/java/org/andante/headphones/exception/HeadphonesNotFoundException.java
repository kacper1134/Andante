package org.andante.headphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HeadphonesNotFoundException extends HeadphonesException {

    public HeadphonesNotFoundException(String message) {
        super(message);
    }
}
