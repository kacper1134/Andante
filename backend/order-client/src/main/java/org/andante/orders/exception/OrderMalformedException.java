package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderMalformedException extends OrderException {

    public OrderMalformedException(String message) {
        super(message);
    }
}
