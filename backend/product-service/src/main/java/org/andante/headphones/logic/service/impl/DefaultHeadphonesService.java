package org.andante.headphones.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.headphones.exception.HeadphonesConflictException;
import org.andante.headphones.exception.HeadphonesNotFoundException;
import org.andante.headphones.logic.mapper.HeadphonesModelEntityMapper;
import org.andante.headphones.logic.model.HeadphonesInput;
import org.andante.headphones.logic.model.HeadphonesOutput;
import org.andante.headphones.logic.service.HeadphonesService;
import org.andante.headphones.repository.HeadphonesRepository;
import org.andante.headphones.repository.entity.HeadphonesEntity;
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
public class DefaultHeadphonesService implements HeadphonesService {

    private static final String HEADPHONES_CONFLICT_EXCEPTION_MESSAGE = "Headphones with identifier %d already exist";
    private static final String HEADPHONES_NOT_FOUND_EXCEPTION_MESSAGE = "Headphones with identifier %d do not exist";

    private final HeadphonesRepository headphonesRepository;
    private final HeadphonesModelEntityMapper headphonesModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<HeadphonesEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<HeadphonesOutput> findAllById(Set<Long> identifiers) {
        List<HeadphonesEntity> databaseResponse = headphonesRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(headphonesModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<HeadphonesOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<HeadphonesEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = headphonesRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<HeadphonesEntity> databaseResponse = headphonesRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(headphonesModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public HeadphonesOutput create(HeadphonesInput headphonesInput) {
        if (headphonesInput.getId() != null && headphonesRepository.existsById(headphonesInput.getId())) {
            throw new HeadphonesConflictException(String.format(HEADPHONES_CONFLICT_EXCEPTION_MESSAGE, headphonesInput.getId()));
        }

        HeadphonesEntity headphonesEntity = headphonesModelEntityMapper.toEntity(headphonesInput);
        HeadphonesEntity createdHeadphones = headphonesRepository.save(headphonesEntity);

        return headphonesModelEntityMapper.toModel(createdHeadphones);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public HeadphonesOutput modify(HeadphonesInput headphonesInput) {
        if (headphonesInput.getId() == null || !headphonesRepository.existsById(headphonesInput.getId())) {
            throw new HeadphonesNotFoundException(String.format(HEADPHONES_NOT_FOUND_EXCEPTION_MESSAGE, headphonesInput.getId()));
        }

        HeadphonesEntity headphonesEntity = headphonesModelEntityMapper.toEntity(headphonesInput);

        HeadphonesEntity updatedHeadphones = headphonesRepository.save(headphonesEntity);

        return headphonesModelEntityMapper.toModel(updatedHeadphones);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public HeadphonesOutput delete(Long identifier) {
        Optional<HeadphonesEntity> databaseResponse = headphonesRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new HeadphonesNotFoundException(String.format(HEADPHONES_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        headphonesRepository.delete(databaseResponse.get());

        return headphonesModelEntityMapper.toModel(databaseResponse.get());
    }
}
