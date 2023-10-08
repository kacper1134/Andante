package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientConflictException extends ClientException {

    public ClientConflictException(String message) {
        super(message);
    }
}
