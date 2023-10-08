package org.andante.orders.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum LocationSortingOrder {

    ALPHABETICAL_STREET,
    ALPHABETICAL_POSTCODE;

    @JsonCreator
    public static LocationSortingOrder deserialize(String value) {
        return EnumSet.allOf(LocationSortingOrder.class).stream()
                .filter(locationSortingOrder -> locationSortingOrder.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining("_"));
    }
}
