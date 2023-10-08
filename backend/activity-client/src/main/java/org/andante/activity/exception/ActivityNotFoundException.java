package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActivityNotFoundException extends ActivityException {

    public ActivityNotFoundException(String message) {
        super(message);
    }
}
