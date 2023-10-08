package org.andante.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.RequiredArgsConstructor;
import org.andante.exception.RSQLException;
import org.andante.rsql.operator.RSQLSearchOperator;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RSQLSpecification<T> implements Specification<T> {

    private static final String RSQL_MAPPING_ERROR_MESSAGE = "Operator %s was not recognized as valid RSQL operator!";
    private static final String DATABASE_WILDCARD = "%";
    private static final String RSQL_WILDCARD = "*";

    private final String property;
    private final transient ComparisonOperator operator;
    private final List<String> arguments;

    @Override
    public Predicate toPredicate(Root<T> root,CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Object> typedArguments = castArguments(root);
        Object singularArgument = typedArguments.get(0);
        RSQLSearchOperator searchOperator = RSQLSearchOperator.map(operator).orElseThrow(() -> new RSQLException(String.format(RSQL_MAPPING_ERROR_MESSAGE, operator)));

        switch (searchOperator) {
            case EQUAL:
                return equalPredicate(criteriaBuilder, root, singularArgument);
            case NOT_EQUAL:
                return notEqualPredicate(criteriaBuilder, root, singularArgument);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(property), singularArgument.toString());
            case GREATER_THAN_OR_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(property), singularArgument.toString());
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(property), singularArgument.toString());
            case LESS_THAN_OR_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(root.get(property), singularArgument.toString());
            case IN:
                return root.get(property).in(typedArguments);
            case NOT_IN:
                return criteriaBuilder.not(root.get(property).in(typedArguments));
        }

        throw new RSQLException(String.format(RSQL_MAPPING_ERROR_MESSAGE, operator));
    }

    private Predicate equalPredicate(CriteriaBuilder criteriaBuilder, Root<T> root, Object argument) {
        if (argument == null) {
            return criteriaBuilder.isNull(root.get(property));
        } else if (argument instanceof String) {
            return criteriaBuilder.like(root.get(property), argument.toString());
        }

        return criteriaBuilder.equal(root.get(property), argument);
    }

    private Predicate notEqualPredicate(CriteriaBuilder criteriaBuilder, Root<T> root, Object argument) {
        if (argument == null) {
            return criteriaBuilder.isNotNull(root.get(property));
        } else if (argument instanceof String) {
            return criteriaBuilder.notLike(root.get(property), argument.toString());
        }

        return criteriaBuilder.notEqual(root.get(property), argument.toString());
    }

    @SuppressWarnings({"unchecked", "unchecked"})
    private List<Object> castArguments(Root<T> root) {
        Class<?> type = root.get(property).getJavaType();

        return arguments.stream()
                .map(argument -> URLDecoder.decode(argument, StandardCharsets.UTF_8))
                .map(argument -> argument.replace(RSQL_WILDCARD, DATABASE_WILDCARD))
                .map(argument -> {
                    if (argument.equals("null")) {
                        return null;
                    }
                    else if (type.equals(Integer.class)) {
                        return Integer.parseInt(argument);
                    } else if (type.equals(Long.class)) {
                        return Long.parseLong(argument);
                    } else if (type.equals(Float.class)) {
                        return Float.parseFloat(argument);
                    } else if (type.equals(Boolean.class)) {
                        return Boolean.parseBoolean(argument);
                    } else if (type.isEnum()) {
                        return Enum.valueOf((Class<? extends Enum>) type, argument);
                    }
                    return argument;
                }).collect(Collectors.toList());
    }
}
