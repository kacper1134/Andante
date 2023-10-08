package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActivityException extends RuntimeException {

    public ActivityException(String message) {
        super(message);
    }
}
