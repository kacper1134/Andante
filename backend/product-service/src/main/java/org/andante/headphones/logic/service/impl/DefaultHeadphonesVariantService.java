package org.andante.headphones.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.headphones.exception.HeadphonesNotFoundException;
import org.andante.headphones.exception.HeadphonesVariantConflictException;
import org.andante.headphones.logic.mapper.HeadphonesVariantModelEntityMapper;
import org.andante.headphones.logic.model.HeadphonesVariantInput;
import org.andante.headphones.logic.model.HeadphonesVariantOutput;
import org.andante.headphones.logic.service.HeadphonesVariantService;
import org.andante.headphones.repository.HeadphonesVariantRepository;
import org.andante.headphones.repository.entity.HeadphonesVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ =  @Autowired)
public class DefaultHeadphonesVariantService implements HeadphonesVariantService {

    private static final String HEADPHONES_VARIANT_CONFLICT_EXCEPTION_MESSAGE = "Headphones variant with identifier %d already exists";
    private static final String HEADPHONES_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Headphones variant with identifier %d does not exist";

    private final HeadphonesVariantRepository headphonesVariantRepository;
    private final HeadphonesVariantModelEntityMapper headphonesVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<HeadphonesVariantOutput> getAllById(Set<Long> identifiers) {
        List<HeadphonesVariantEntity> databaseResponse = headphonesVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(headphonesVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<HeadphonesVariantOutput> getAllByHeadphonesId(Long headphonesId) {
        Set<HeadphonesVariantEntity> databaseResponse = headphonesVariantRepository.findAllByHeadphonesId(headphonesId);

        return databaseResponse.stream()
                .map(headphonesVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public HeadphonesVariantOutput create(HeadphonesVariantInput headphonesVariant) {
        if (headphonesVariant.getId() != null && headphonesVariantRepository.existsById(headphonesVariant.getId())) {
            throw new HeadphonesVariantConflictException(String.format(HEADPHONES_VARIANT_CONFLICT_EXCEPTION_MESSAGE, headphonesVariant.getId()));
        }

        HeadphonesVariantEntity headphonesVariantEntity = headphonesVariantModelEntityMapper.toEntity(headphonesVariant);

        HeadphonesVariantEntity createdHeadphonesVariant = headphonesVariantRepository.save(headphonesVariantEntity);

        return headphonesVariantModelEntityMapper.toModel(createdHeadphonesVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public HeadphonesVariantOutput modify(HeadphonesVariantInput headphonesVariant) {
        if (headphonesVariant.getId() == null || !headphonesVariantRepository.existsById(headphonesVariant.getId())) {
            throw new HeadphonesNotFoundException(String.format(HEADPHONES_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, headphonesVariant.getId()));
        }

        HeadphonesVariantEntity headphonesVariantEntity = headphonesVariantModelEntityMapper.toEntity(headphonesVariant);

        HeadphonesVariantEntity modifiedHeadphonesVariant = headphonesVariantRepository.save(headphonesVariantEntity);

        return headphonesVariantModelEntityMapper.toModel(modifiedHeadphonesVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public HeadphonesVariantOutput delete(Long identifier) {
        Optional<HeadphonesVariantEntity> databaseResponse = headphonesVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new HeadphonesNotFoundException(String.format(HEADPHONES_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        headphonesVariantRepository.delete(databaseResponse.get());

        return headphonesVariantModelEntityMapper.toModel(databaseResponse.get());
    }
}
