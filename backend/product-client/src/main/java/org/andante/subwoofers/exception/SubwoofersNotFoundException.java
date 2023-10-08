package org.andante.subwoofers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubwoofersNotFoundException extends SubwoofersException {

    public SubwoofersNotFoundException(String message) {
        super(message);
    }
}
