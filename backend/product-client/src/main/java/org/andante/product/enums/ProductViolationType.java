package org.andante.product.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum ProductViolationType {

    MISSING_PRODUCT_VARIANT,
    INSUFFICIENT_PRODUCT_VARIANT_QUANTITY;

    @JsonCreator
    public static ProductViolationType deserialize(String value) {
        return EnumSet.allOf(ProductViolationType.class).stream()
                .filter(type -> type.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(""));
    }
}
