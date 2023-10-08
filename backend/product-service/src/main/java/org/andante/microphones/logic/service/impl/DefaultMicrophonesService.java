package org.andante.microphones.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.microphones.exception.MicrophonesConflictException;
import org.andante.microphones.exception.MicrophonesNotFoundException;
import org.andante.microphones.logic.mapper.MicrophonesModelEntityMapper;
import org.andante.microphones.logic.model.MicrophonesInput;
import org.andante.microphones.logic.model.MicrophonesOutput;
import org.andante.microphones.logic.service.MicrophonesService;
import org.andante.microphones.repository.MicrophonesRepository;
import org.andante.microphones.repository.entity.MicrophonesEntity;
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
public class DefaultMicrophonesService implements MicrophonesService {

    private static final String MICROPHONES_CONFLICT_EXCEPTION_MESSAGE = "Microphones with identifier %d already exist";
    private static final String MICROPHONES_NOT_FOUND_EXCEPTION_MESSAGE = "Microphones with identifier %d do not exist";

    private final MicrophonesRepository microphonesRepository;
    private final MicrophonesModelEntityMapper microphonesModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<MicrophonesEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<MicrophonesOutput> getAllByIds(Set<Long> identifiers) {
        List<MicrophonesEntity> databaseResponse = microphonesRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(microphonesModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<MicrophonesOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<MicrophonesEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = microphonesRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<MicrophonesEntity> databaseResponse = microphonesRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(microphonesModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MicrophonesOutput create(MicrophonesInput microphonesInput) {
        if (microphonesInput.getId() != null && microphonesRepository.existsById(microphonesInput.getId())) {
            throw new MicrophonesConflictException(String.format(MICROPHONES_CONFLICT_EXCEPTION_MESSAGE, microphonesInput.getId()));
        }

        MicrophonesEntity microphonesEntity = microphonesModelEntityMapper.toEntity(microphonesInput);
        MicrophonesEntity createdMicrophones =  microphonesRepository.save(microphonesEntity);

        return microphonesModelEntityMapper.toModel(createdMicrophones);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MicrophonesOutput modify(MicrophonesInput microphonesInput) {
        if (microphonesInput.getId() == null || !microphonesRepository.existsById(microphonesInput.getId())) {
            throw new MicrophonesNotFoundException(String.format(MICROPHONES_NOT_FOUND_EXCEPTION_MESSAGE, microphonesInput.getId()));
        }

        MicrophonesEntity microphonesEntity = microphonesModelEntityMapper.toEntity(microphonesInput);

        MicrophonesEntity modifiedMicrophones = microphonesRepository.save(microphonesEntity);

        return microphonesModelEntityMapper.toModel(modifiedMicrophones);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MicrophonesOutput delete(Long identifier) {
        Optional<MicrophonesEntity> databaseResponse = microphonesRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new MicrophonesNotFoundException(String.format(MICROPHONES_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        microphonesRepository.delete(databaseResponse.get());

        return microphonesModelEntityMapper.toModel(databaseResponse.get());
    }
}
