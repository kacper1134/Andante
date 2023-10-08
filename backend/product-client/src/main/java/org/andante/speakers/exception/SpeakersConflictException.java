package org.andante.speakers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpeakersConflictException extends SpeakersException {

    public SpeakersConflictException(String message) {
        super(message);
    }
}
