package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserConflictException extends RuntimeException {

    public UserConflictException(String message) {
        super(message);
    }
}
