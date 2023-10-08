package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderCommunicationException extends RuntimeException{

    public OrderCommunicationException(String message) {
        super(message);
    }
}
