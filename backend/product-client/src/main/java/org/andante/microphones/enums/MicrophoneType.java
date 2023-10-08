package org.andante.microphones.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum MicrophoneType {
    DYNAMIC,
    LARGE_CONDENSER,
    SMALL_CONDENSER,
    RIBBON;


    @JsonCreator
    public static MicrophoneType deserialize(String value) {
        return EnumSet.allOf(MicrophoneType.class).stream()
                .filter(microphoneType -> microphoneType.toString().equals(value))
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
