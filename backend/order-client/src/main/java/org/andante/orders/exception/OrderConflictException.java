package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderConflictException extends OrderException{

    public OrderConflictException(String message) {
        super(message);
    }
}
