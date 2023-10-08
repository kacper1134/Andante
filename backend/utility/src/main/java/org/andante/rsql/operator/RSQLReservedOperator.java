package org.andante.rsql.operator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RSQLReservedOperator {
    QUOTE("\""),
    APOSTROPHE("'"),
    LEFT_BRACKET("("),
    RIGHT_BRACKET(")"),
    SEMICOLON(";"),
    COMMA(","),
    EQUALS("="),
    EXCLAMATION_MARK("!"),
    TILDE("~"),
    LESS_THAN("<"),
    GREATER_THAN(">");

    private final String reservedCharacter;
}
