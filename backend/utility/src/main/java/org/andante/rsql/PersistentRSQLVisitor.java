package org.andante.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PersistentRSQLVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    private final RSQLSpecificationBuilder<T> specificationBuilder;

    @Override
    public Specification<T> visit(AndNode andNode, Void unused) {
        return specificationBuilder.build(andNode);
    }

    @Override
    public Specification<T> visit(OrNode orNode, Void unused) {
        return specificationBuilder.build(orNode);
    }

    @Override
    public Specification<T> visit(ComparisonNode comparisonNode, Void unused) {
        return specificationBuilder.build(comparisonNode);
    }
}
