package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewsletterConflictException extends NewsletterException {

    public NewsletterConflictException(String message) {
        super(message);
    }
}
