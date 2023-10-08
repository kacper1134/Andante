package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewsletterException extends RuntimeException {

    public NewsletterException(String message) {
        super(message);
    }
}
