package org.andante.headphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HeadphonesException extends RuntimeException {

    public HeadphonesException(String message) {
        super(message);
    }
}
