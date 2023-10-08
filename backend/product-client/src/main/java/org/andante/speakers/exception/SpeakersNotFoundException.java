package org.andante.speakers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpeakersNotFoundException extends SpeakersException {

    public SpeakersNotFoundException(String message) {
        super(message);
    }
}
