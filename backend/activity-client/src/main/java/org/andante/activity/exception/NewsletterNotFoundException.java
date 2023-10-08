package org.andante.activity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewsletterNotFoundException extends NewsletterException {

    public NewsletterNotFoundException(String message) {
        super(message);
    }
}
