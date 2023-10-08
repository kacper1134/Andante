package org.andante.product.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum ProductSortingOrder {
    PRICE_ASCENDING,
    PRICE_DESCENDING,
    RECENTLY_ADDED,
    ALPHABETICAL;

    @JsonCreator
    public static ProductSortingOrder deserialize(String value) {
        return EnumSet.allOf(ProductSortingOrder.class).stream()
                .filter(productSortingOrder -> productSortingOrder.toString().equals(value))
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
