package org.andante.product.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommentConflictException extends CommentException {

    public CommentConflictException(String message) {
        super(message);
    }
}
