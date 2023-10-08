package org.andante.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RSQLSpecificationBuilder<T> {

    public Specification<T> build(Node node) {
        if (node instanceof LogicalNode) {
            return buildSpecification((LogicalNode)node);
        } else if (node instanceof ComparisonNode) {
            return buildSpecification((ComparisonNode)node);
        }

        return null;
    }

    private Specification<T> buildSpecification(LogicalNode node) {
        List<Specification<T>> specifications = node.getChildren().stream()
                .map(this::build)
                .collect(Collectors.toList());

        Specification<T> result = specifications.get(0);

        for (int i = 1; i < specifications.size(); i++) {
            Specification<T> current = specifications.get(i);

            if (node.getOperator().equals(LogicalOperator.AND)) {
                result = Specification.where(result).and(current);
            } else {
                result = Specification.where(result).or(current);
            }
        }

        return result;
    }

    private Specification<T> buildSpecification(ComparisonNode node) {
        return new RSQLSpecification<>(node.getSelector(), node.getOperator(), node.getArguments());
    }
}
