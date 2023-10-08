package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderNotFoundException extends OrderException{

    public OrderNotFoundException(String message) {
        super(message);
    }
}
