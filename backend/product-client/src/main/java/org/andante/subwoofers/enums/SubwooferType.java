package org.andante.subwoofers.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum SubwooferType {
    ACTIVE,
    PASSIVE,
    PORTED,
    SEALED_CABINET,
    PASSIVE_RADIATOR,
    BANDPASS,
    HORN_LOADED;

    @JsonCreator
    public static SubwooferType deserialize(String value) {
        return EnumSet.allOf(SubwooferType.class).stream()
                .filter(subwooferType -> subwooferType.toString().equals(value))
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
