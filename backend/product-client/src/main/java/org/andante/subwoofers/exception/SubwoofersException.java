package org.andante.subwoofers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubwoofersException extends RuntimeException {

    public SubwoofersException(String message) {
        super(message);
    }
}
