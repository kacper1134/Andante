package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OrderEntryConflictException extends OrderEntryException{

    public OrderEntryConflictException(String message) {
        super(message);
    }

}
