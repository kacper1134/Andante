package org.andante.headphones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum ConstructionType {
    OPENED,
    SEMI_OPENED,
    CLOSED;

    @JsonCreator
    public static ConstructionType deserialize(String value) {
        return EnumSet.allOf(ConstructionType.class).stream()
                .filter(constructionType -> constructionType.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining("-"));
    }
}
