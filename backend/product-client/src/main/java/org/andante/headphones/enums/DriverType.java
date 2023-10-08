package org.andante.headphones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum DriverType {
    DYNAMIC,
    PLANAR,
    ELECTROSTATIC,
    BALANCED_ARMATURE,
    BONE_CONDUCTION;

    @JsonCreator
    public static DriverType deserialize(String value) {
        return EnumSet.allOf(DriverType.class).stream()
                .filter(driverType -> driverType.toString().equals(value))
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
