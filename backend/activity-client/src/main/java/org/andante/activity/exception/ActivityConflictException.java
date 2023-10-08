package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActivityConflictException extends ActivityException {

    public ActivityConflictException(String message) {
        super(message);
    }
}
