package org.andante.gramophones.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.gramophones.exception.GramophonesVariantConflictException;
import org.andante.gramophones.exception.GramophonesVariantNotFoundException;
import org.andante.gramophones.logic.mapper.GramophonesVariantModelEntityMapper;
import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;
import org.andante.gramophones.logic.service.GramophonesVariantService;
import org.andante.gramophones.repository.GramophonesVariantRepository;
import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultGramophonesVariantService implements GramophonesVariantService {

    private static final String GRAMOPHONES_VARIANT_CONFLICT_EXCEPTION_MESSAGE = "Gramophones variant with identifier %d already exists";
    private static final String GRAMOPHONES_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Gramophones variant with identifier %d does not exist";

    private final GramophonesVariantRepository gramophonesVariantRepository;
    private final GramophonesVariantModelEntityMapper gramophonesVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<GramophonesVariantOutput> getAllByIds(Set<Long> identifiers) {
        List<GramophonesVariantEntity> databaseResponse = gramophonesVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(gramophonesVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<GramophonesVariantOutput> getAllByGramophoneId(Long gramophoneId) {
        Set<GramophonesVariantEntity> databaseResponse = gramophonesVariantRepository.findAllByGramophonesId(gramophoneId);

        return databaseResponse.stream()
                .map(gramophonesVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GramophonesVariantOutput create(GramophonesVariantInput gramophonesVariant) {
        if (gramophonesVariant.getId() != null && gramophonesVariantRepository.existsById(gramophonesVariant.getId())) {
            throw new GramophonesVariantConflictException(String.format(GRAMOPHONES_VARIANT_CONFLICT_EXCEPTION_MESSAGE, gramophonesVariant.getId()));
        }

        GramophonesVariantEntity gramophonesVariantToCreate = gramophonesVariantModelEntityMapper.toEntity(gramophonesVariant);
        GramophonesVariantEntity createdGramophonesVariant = gramophonesVariantRepository.save(gramophonesVariantToCreate);

        return gramophonesVariantModelEntityMapper.toModel(createdGramophonesVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GramophonesVariantOutput modify(GramophonesVariantInput gramophonesVariant) {
        if (gramophonesVariant.getId() == null || !gramophonesVariantRepository.existsById(gramophonesVariant.getId())) {
            throw new GramophonesVariantNotFoundException(String.format(GRAMOPHONES_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, gramophonesVariant.getId()));
        }

        GramophonesVariantEntity gramophonesVariantToModify = gramophonesVariantModelEntityMapper.toEntity(gramophonesVariant);

        GramophonesVariantEntity modifiedGramophonesVariant = gramophonesVariantRepository.save(gramophonesVariantToModify);

        return gramophonesVariantModelEntityMapper.toModel(modifiedGramophonesVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public GramophonesVariantOutput delete(Long identifier) {
        Optional<GramophonesVariantEntity> databaseResponse = gramophonesVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new GramophonesVariantNotFoundException(String.format(GRAMOPHONES_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        gramophonesVariantRepository.delete(databaseResponse.get());

        return gramophonesVariantModelEntityMapper.toModel(databaseResponse.get());
    }
}
