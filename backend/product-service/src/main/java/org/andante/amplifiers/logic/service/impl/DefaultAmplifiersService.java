package org.andante.amplifiers.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.exception.AmplifiersConflictException;
import org.andante.amplifiers.exception.AmplifiersNotFoundException;
import org.andante.amplifiers.logic.mapper.AmplifiersModelEntityMapper;
import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.amplifiers.logic.service.AmplifiersService;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DefaultAmplifiersService implements AmplifiersService {

    private static final String AMPLIFIERS_NOT_FOUND_EXCEPTION_MESSAGE = "Amplifiers with identifier %d do not exist";
    private static final String AMPLIFIERS_PRESENT_EXCEPTION_MESSAGE = "Amplifiers with identifier %d already exist";

    private final AmplifiersRepository amplifiersRepository;
    private final AmplifiersModelEntityMapper amplifiersModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<AmplifiersEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<AmplifiersOutput> getAllById(Set<Long> identifiers) {
        List<AmplifiersEntity> databaseResponse = amplifiersRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(amplifiersModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<AmplifiersOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<AmplifiersEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = amplifiersRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<AmplifiersEntity> databaseResponse = amplifiersRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(amplifiersModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AmplifiersOutput create(AmplifiersInput amplifiersInput) {
        if (amplifiersInput.getId() != null && amplifiersRepository.existsById(amplifiersInput.getId())) {
            throw new AmplifiersConflictException(String.format(AMPLIFIERS_PRESENT_EXCEPTION_MESSAGE, amplifiersInput.getId()));
        }

        AmplifiersEntity amplifiersToCreate = amplifiersModelEntityMapper.toEntity(amplifiersInput);
        AmplifiersEntity amplifiersCreated = amplifiersRepository.save(amplifiersToCreate);

        return amplifiersModelEntityMapper.toModel(amplifiersCreated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AmplifiersOutput update(AmplifiersInput amplifiersInput) {
        if (amplifiersInput.getId() == null || !amplifiersRepository.existsById(amplifiersInput.getId())) {
            throw new AmplifiersNotFoundException(String.format(AMPLIFIERS_NOT_FOUND_EXCEPTION_MESSAGE, amplifiersInput.getId()));
        }

        AmplifiersEntity amplifiersToUpdate = amplifiersModelEntityMapper.toEntity(amplifiersInput);
        AmplifiersEntity amplifiersUpdated = amplifiersRepository.save(amplifiersToUpdate);

        return amplifiersModelEntityMapper.toModel(amplifiersUpdated);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AmplifiersOutput delete(Long identifier) {
        Optional<AmplifiersEntity> databaseResponse = amplifiersRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new AmplifiersNotFoundException(String.format(AMPLIFIERS_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        amplifiersRepository.delete(databaseResponse.get());

        return amplifiersModelEntityMapper.toModel(databaseResponse.get());
    }
}
