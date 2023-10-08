package org.andante.activity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum Priority {
    HIGHEST,
    HIGH,
    MEDIUM,
    LOW,
    LOWEST;

    @JsonCreator
    public static Priority deserialize(String value) {
        return EnumSet.allOf(Priority.class).stream()
                .filter(priority -> priority.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
