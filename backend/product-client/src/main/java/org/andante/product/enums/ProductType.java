package org.andante.product.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.EnumSet;

public enum ProductType {
    AMPLIFIERS,
    GRAMOPHONES,
    HEADPHONES,
    MICROPHONES,
    SPEAKERS,
    SUBWOOFERS;

    @JsonCreator
    public static ProductType deserialize(String value) {
        return EnumSet.allOf(ProductType.class).stream()
                .filter(productType -> productType.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
