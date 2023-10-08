package org.andante.rsql.operator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
public enum RSQLSearchOperator {
    EQUAL(RSQLOperators.EQUAL),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    LESS_THAN(RSQLOperators.LESS_THAN),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    IN(RSQLOperators.IN),
    NOT_IN(RSQLOperators.NOT_IN);

    private final ComparisonOperator operator;

    public static Optional<RSQLSearchOperator> map(ComparisonOperator operator) {
        return EnumSet.allOf(RSQLSearchOperator.class).stream()
                .filter(searchOperator -> searchOperator.getOperator().equals(operator))
                .findAny();
    }
}
