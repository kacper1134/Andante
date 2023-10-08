package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LocationConflictException extends LocationException{

    public LocationConflictException(String message) {
        super(message);
    }
}
