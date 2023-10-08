package org.andante.orders.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LocationNotFoundException extends LocationException{

    public LocationNotFoundException(String message) {
        super(message);
    }
}
