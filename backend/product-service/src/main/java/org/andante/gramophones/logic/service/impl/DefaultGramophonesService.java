package org.andante.gramophones.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.gramophones.exception.GramophonesConflictException;
import org.andante.gramophones.exception.GramophonesNotFoundException;
import org.andante.gramophones.logic.mapper.GramophonesModelEntityMapper;
import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesOutput;
import org.andante.gramophones.logic.service.GramophonesService;
import org.andante.gramophones.repository.GramophonesRepository;
import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.logic.specification.ProductSpecificationBuilder;
import org.andante.rsql.PersistentRSQLVisitor;
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
public class DefaultGramophonesService implements GramophonesService {

    private static final String GRAMOPHONES_CONFLICT_EXCEPTION_MESSAGE = "Gramophones with identifier %d already exist";
    private static final String GRAMOPHONES_NOT_FOUND_EXCEPTION_MESSAGE = "Gramophones with identifier %d do not exist";

    private final GramophonesRepository gramophonesRepository;
    private final GramophonesModelEntityMapper gramophonesModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<GramophonesEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<GramophonesOutput> getAllById(Set<Long> identifiers) {
        List<GramophonesEntity> databaseResponse = gramophonesRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(gramophonesModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<GramophonesOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<GramophonesEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = gramophonesRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<GramophonesEntity> databaseResponse = gramophonesRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(gramophonesModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GramophonesOutput create(GramophonesInput gramophonesInput) {
        if (gramophonesInput.getId() != null && gramophonesRepository.existsById(gramophonesInput.getId())) {
            throw new GramophonesConflictException(String.format(GRAMOPHONES_CONFLICT_EXCEPTION_MESSAGE, gramophonesInput.getId()));
        }

        GramophonesEntity gramophonesToCreate = gramophonesModelEntityMapper.toEntity(gramophonesInput);
        GramophonesEntity createdGramophones = gramophonesRepository.save(gramophonesToCreate);

        return gramophonesModelEntityMapper.toModel(createdGramophones);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GramophonesOutput modify(GramophonesInput gramophonesInput) {
        if (gramophonesInput.getId() == null || !gramophonesRepository.existsById(gramophonesInput.getId())) {
            throw new GramophonesNotFoundException(String.format(GRAMOPHONES_NOT_FOUND_EXCEPTION_MESSAGE, gramophonesInput.getId()));
        }

        GramophonesEntity gramophonesToUpdate = gramophonesModelEntityMapper.toEntity(gramophonesInput);
        GramophonesEntity updatedGramophones = gramophonesRepository.save(gramophonesToUpdate);

        return gramophonesModelEntityMapper.toModel(updatedGramophones);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GramophonesOutput delete(Long identifier) {
        Optional<GramophonesEntity> databaseResponse = gramophonesRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new GramophonesNotFoundException(String.format(GRAMOPHONES_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        gramophonesRepository.delete(databaseResponse.get());

        return gramophonesModelEntityMapper.toModel(databaseResponse.get());
    }
}
