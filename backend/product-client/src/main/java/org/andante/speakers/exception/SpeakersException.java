package org.andante.speakers.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpeakersException extends RuntimeException {

    public SpeakersException(String message) {
        super(message);
    }
}
