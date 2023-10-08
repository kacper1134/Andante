package org.andante.subwoofers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SubwoofersConflictException extends SubwoofersException {

    public SubwoofersConflictException(String message) {
        super(message);
    }
}
