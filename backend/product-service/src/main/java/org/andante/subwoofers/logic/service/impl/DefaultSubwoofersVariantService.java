package org.andante.subwoofers.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.subwoofers.exception.SubwoofersVariantConflictException;
import org.andante.subwoofers.exception.SubwoofersVariantNotFoundException;
import org.andante.subwoofers.logic.mapper.SubwoofersVariantModelEntityMapper;
import org.andante.subwoofers.logic.model.SubwoofersVariantInput;
import org.andante.subwoofers.logic.model.SubwoofersVariantOutput;
import org.andante.subwoofers.logic.service.SubwoofersVariantService;
import org.andante.subwoofers.repository.SubwoofersVariantRepository;
import org.andante.subwoofers.repository.entity.SubwoofersVariantEntity;
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
public class DefaultSubwoofersVariantService implements SubwoofersVariantService {

    private static final String SUBWOOFERS_VARIANT_CONFLICT_EXCEPTION_MESSAGE = "Subwoofers variant with identifier %d already exists";
    private static final String SUBWOOFERS_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Subwoofers variant with identifier %d does not exist";

    private final SubwoofersVariantRepository subwoofersVariantRepository;
    private final SubwoofersVariantModelEntityMapper subwoofersVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<SubwoofersVariantOutput> getAllById(Set<Long> identifiers) {
        List<SubwoofersVariantEntity> databaseResponse = subwoofersVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(subwoofersVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<SubwoofersVariantOutput> getAllBySubwooferId(Long subwooferId) {
        Set<SubwoofersVariantEntity> databaseResponse = subwoofersVariantRepository.findAllBySubwoofersId(subwooferId);

        return databaseResponse.stream()
                .map(subwoofersVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubwoofersVariantOutput create(SubwoofersVariantInput subwoofersVariant) {
        if (subwoofersVariant.getId() != null && subwoofersVariantRepository.existsById(subwoofersVariant.getId())) {
            throw new SubwoofersVariantConflictException(String.format(SUBWOOFERS_VARIANT_CONFLICT_EXCEPTION_MESSAGE, subwoofersVariant.getId()));
        }

        SubwoofersVariantEntity subwoofersVariantEntity = subwoofersVariantModelEntityMapper.toEntity(subwoofersVariant);

        SubwoofersVariantEntity createdSubwoofersVariant = subwoofersVariantRepository.save(subwoofersVariantEntity);

        return subwoofersVariantModelEntityMapper.toModel(createdSubwoofersVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubwoofersVariantOutput modify(SubwoofersVariantInput subwoofersVariant) {
        if (subwoofersVariant.getId() == null || !subwoofersVariantRepository.existsById(subwoofersVariant.getId())) {
            throw new SubwoofersVariantNotFoundException(String.format(SUBWOOFERS_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, subwoofersVariant.getId()));
        }

        SubwoofersVariantEntity subwoofersVariantEntity = subwoofersVariantModelEntityMapper.toEntity(subwoofersVariant);

        SubwoofersVariantEntity modifiedSubwoofersVariant = subwoofersVariantRepository.save(subwoofersVariantEntity);

        return subwoofersVariantModelEntityMapper.toModel(modifiedSubwoofersVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SubwoofersVariantOutput delete(Long identifier) {
        Optional<SubwoofersVariantEntity> databaseResponse = subwoofersVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new SubwoofersVariantConflictException(String.format(SUBWOOFERS_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        subwoofersVariantRepository.delete(databaseResponse.get());

        return subwoofersVariantModelEntityMapper.toModel(databaseResponse.get());
     }
}
