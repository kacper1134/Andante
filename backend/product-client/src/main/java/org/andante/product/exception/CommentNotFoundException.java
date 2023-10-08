package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommentNotFoundException extends CommentException {

    public CommentNotFoundException(String message) {
        super(message);
    }
}
