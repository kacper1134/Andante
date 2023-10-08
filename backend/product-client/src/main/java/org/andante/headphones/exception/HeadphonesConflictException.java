package org.andante.headphones.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HeadphonesConflictException extends HeadphonesException {

    public HeadphonesConflictException(String message) {
        super(message);
    }
}
