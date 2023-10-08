package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderEntryException extends RuntimeException{

    public OrderEntryException(String message) {
        super(message);
    }
}
