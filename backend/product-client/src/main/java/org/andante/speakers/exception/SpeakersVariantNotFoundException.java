package org.andante.speakers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpeakersVariantNotFoundException extends SpeakersException {

    public SpeakersVariantNotFoundException(String message) {
        super(message);
    }
}
