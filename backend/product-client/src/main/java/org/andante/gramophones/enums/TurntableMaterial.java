package org.andante.gramophones.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum TurntableMaterial {
    ACRYLIC,
    ALLOY_STEEL,
    CARBON_FIBER,
    ENGINEERED_WOOD,
    METAL,
    PLASTIC,
    WOOD;

    @JsonCreator
    public static TurntableMaterial deserialize(String value) {
        return EnumSet.allOf(TurntableMaterial.class).stream()
                .filter(turntableMaterial -> turntableMaterial.toString().equals(value))
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
