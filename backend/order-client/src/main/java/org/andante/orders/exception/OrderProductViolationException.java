package org.andante.orders.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class OrderProductViolationException extends RuntimeException {

    private final Set<String> violationMessages;

    public OrderProductViolationException(Set<String> violationMessages) {
        this.violationMessages = violationMessages;
    }
}
