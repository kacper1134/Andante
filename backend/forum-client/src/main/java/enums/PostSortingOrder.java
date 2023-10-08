package enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum PostSortingOrder {

    NEWEST_FIRST,
    OLDEST_FIRST,
    ALPHABETICAL,
    REVERSE_ALPHABETICAL;

    @JsonCreator
    public static PostSortingOrder deserialize(String value) {
        return EnumSet.allOf(PostSortingOrder.class).stream()
                .filter(sortingOrder -> sortingOrder.toString().equals(value))
                .findAny()
                .orElse(null);
    }

    @JsonValue
    @Override
    public String toString() {
        return Arrays.stream(name().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toUpperCase())
                .collect(Collectors.joining("_"));
    }
}
