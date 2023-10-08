package org.andante.amplifiers.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum AmplifierType {
    CURRENT,
    VOLTAGE,
    TRANSCONDUCTANCE,
    TRANSRESISTANCE,
    POWER,
    OPERATIONAL,
    VACUUM_TUBE,
    DISTRIBUTED;

    @JsonCreator
    public static AmplifierType deserialize(String value) {
        return EnumSet.allOf(AmplifierType.class).stream()
                .filter(amplifierType -> amplifierType.toString().equals(value))
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
