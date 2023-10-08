package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientNotFoundException extends ClientException {

    public ClientNotFoundException(String message) {
        super(message);
    }
}
