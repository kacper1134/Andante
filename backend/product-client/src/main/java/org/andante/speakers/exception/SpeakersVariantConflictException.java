package org.andante.speakers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpeakersVariantConflictException extends SpeakersException {

    public SpeakersVariantConflictException(String message) {
        super(message);
    }
}
