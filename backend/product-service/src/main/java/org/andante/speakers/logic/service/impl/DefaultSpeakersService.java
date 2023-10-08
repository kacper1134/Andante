package org.andante.speakers.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.logic.specification.ProductSpecificationBuilder;
import org.andante.rsql.PersistentRSQLVisitor;
import org.andante.speakers.exception.SpeakersConflictException;
import org.andante.speakers.exception.SpeakersNotFoundException;
import org.andante.speakers.logic.mapper.SpeakersModelEntityMapper;
import org.andante.speakers.logic.model.SpeakersInput;
import org.andante.speakers.logic.model.SpeakersOutput;
import org.andante.speakers.logic.service.SpeakersService;
import org.andante.speakers.repository.SpeakersRepository;
import org.andante.speakers.repository.entity.SpeakersEntity;
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
public class DefaultSpeakersService implements SpeakersService {

    private static final String SPEAKERS_CONFLICT_EXCEPTION_MESSAGE = "Speakers with identifier %d already exist";
    private static final String SPEAKERS_NOT_FOUND_EXCEPTION_MESSAGE = "Speakers with identifier %d do not exist";

    private final SpeakersRepository speakersRepository;
    private final SpeakersModelEntityMapper speakersModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<SpeakersEntity> rsqlVisitor;
    private final ProductSpecificationBuilder specificationBuilder;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<SpeakersOutput> getAllById(Set<Long> identifiers) {
        List<SpeakersEntity> databaseResponse = speakersRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(speakersModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<SpeakersOutput> getByQuery(ProductQuerySpecification productQuerySpecification, Double minimumRating) {
        Node rootNode = rsqlParser.parse(productQuerySpecification.getQuery());
        Specification<SpeakersEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = specificationBuilder.getPageSpecification(productQuerySpecification);

        Set<Long> ids = speakersRepository.findByMinimumRating(minimumRating);

        specification = specification.and((root, query, criteriaBuilder) -> root.get("id").in(ids));

        Page<SpeakersEntity> databaseResponse = speakersRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(speakersModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SpeakersOutput create(SpeakersInput speakersInput) {
        if (speakersInput.getId() != null && speakersRepository.existsById(speakersInput.getId())) {
            throw new SpeakersConflictException(String.format(SPEAKERS_CONFLICT_EXCEPTION_MESSAGE, speakersInput.getId()));
        }

        SpeakersEntity speakersEntity = speakersModelEntityMapper.toEntity(speakersInput);

        SpeakersEntity createdSpeakers = speakersRepository.save(speakersEntity);

        return speakersModelEntityMapper.toModel(createdSpeakers);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SpeakersOutput modify(SpeakersInput speakersInput) {
        if (speakersInput.getId() == null || !speakersRepository.existsById(speakersInput.getId())) {
            throw new SpeakersNotFoundException(String.format(SPEAKERS_NOT_FOUND_EXCEPTION_MESSAGE, speakersInput.getId()));
        }

        SpeakersEntity speakersEntity = speakersModelEntityMapper.toEntity(speakersInput);

        SpeakersEntity modifiedSpeakers = speakersRepository.save(speakersEntity);

        return speakersModelEntityMapper.toModel(modifiedSpeakers);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SpeakersOutput delete(Long identifier) {
        Optional<SpeakersEntity> databaseResponse = speakersRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new SpeakersNotFoundException(String.format(SPEAKERS_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        speakersRepository.delete(databaseResponse.get());

        return speakersModelEntityMapper.toModel(databaseResponse.get());
    }
}
