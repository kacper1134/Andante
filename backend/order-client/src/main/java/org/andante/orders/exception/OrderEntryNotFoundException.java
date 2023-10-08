package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderEntryNotFoundException extends OrderEntryException{

    public OrderEntryNotFoundException(String message) {
        super(message);
    }
}
