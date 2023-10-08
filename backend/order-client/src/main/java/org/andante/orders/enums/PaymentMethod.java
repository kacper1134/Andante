package org.andante.orders.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum PaymentMethod {
    GOOGLE_PAY,
    VISA,
    PAY_U;

    @JsonCreator
    public static PaymentMethod deserialize(String value) {
        return EnumSet.allOf(PaymentMethod.class).stream()
                .filter(paymentMethod -> paymentMethod.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(""));
    }
}
