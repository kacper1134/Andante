package org.andante.subwoofers.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.logic.specification.ProductSpecificationBuilder;
import org.andante.rsql.PersistentRSQLVisitor;
import org.andante.subwoofers.exception.SubwoofersConflictException;
import org.andante.subwoofers.exception.SubwoofersNotFoundException;
import org.andante.subwoofers.logic.mapper.SubwoofersModelEntityMapper;
import org.andante.subwoofers.logic.model.SubwoofersInput;
import org.andante.subwoofers.logic.model.SubwoofersOutput;
import org.andante.subwoofers.logic.service.SubwoofersService;
import org.andante.subwoofers.repository.SubwoofersRepository;
import org.andante.subwoofers.repository.entity.SubwoofersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultSubwoofersService implements SubwoofersService {

    private static final String SUBWOOFERS_CONFLICT_EXCEPTION_MESSAGE = "Subwoofers with identifier %d already exist";
    private static final String SUBWOOFERS_NOT_FOUND_EXCEPTION_MESSAGE = "Subwoofers with identifier %d do not exist";

    private final SubwoofersRepository subwoofersRepository;
    private final SubwoofersModelEntityMapper subwoofersModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<SubwoofersEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<SubwoofersOutput> getAllById(Set<Long> identifiers) {
        List<SubwoofersEntity> databaseResponse = subwoofersRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(subwoofersModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<SubwoofersOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<SubwoofersEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = subwoofersRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<SubwoofersEntity> databaseResponse = subwoofersRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(subwoofersModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubwoofersOutput create(SubwoofersInput subwoofersInput) {
        if (subwoofersInput.getId() != null && subwoofersRepository.existsById(subwoofersInput.getId())) {
            throw new SubwoofersConflictException(String.format(SUBWOOFERS_CONFLICT_EXCEPTION_MESSAGE, subwoofersInput.getId()));
        }

        SubwoofersEntity subwoofersEntity = subwoofersModelEntityMapper.toEntity(subwoofersInput);

        SubwoofersEntity createdSubwoofers = subwoofersRepository.save(subwoofersEntity);

        return subwoofersModelEntityMapper.toModel(createdSubwoofers);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubwoofersOutput modify(SubwoofersInput subwoofersInput) {
        if (subwoofersInput.getId() == null || !subwoofersRepository.existsById(subwoofersInput.getId())) {
            throw new SubwoofersNotFoundException(String.format(SUBWOOFERS_NOT_FOUND_EXCEPTION_MESSAGE, subwoofersInput.getId()));
        }

        SubwoofersEntity subwoofersEntity = subwoofersModelEntityMapper.toEntity(subwoofersInput);

        SubwoofersEntity updatedSubwoofers = subwoofersRepository.save(subwoofersEntity);

        return subwoofersModelEntityMapper.toModel(updatedSubwoofers);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubwoofersOutput delete(Long identifier) {
        Optional<SubwoofersEntity> databaseResponse = subwoofersRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new SubwoofersNotFoundException(String.format(SUBWOOFERS_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        subwoofersRepository.delete(databaseResponse.get());

        return subwoofersModelEntityMapper.toModel(databaseResponse.get());
    }
}
