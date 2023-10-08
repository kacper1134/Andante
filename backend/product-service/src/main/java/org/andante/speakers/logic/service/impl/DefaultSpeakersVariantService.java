package org.andante.speakers.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.speakers.exception.SpeakersVariantConflictException;
import org.andante.speakers.exception.SpeakersVariantNotFoundException;
import org.andante.speakers.logic.mapper.SpeakersVariantModelEntityMapper;
import org.andante.speakers.logic.model.SpeakersVariantInput;
import org.andante.speakers.logic.model.SpeakersVariantOutput;
import org.andante.speakers.logic.service.SpeakersVariantService;
import org.andante.speakers.repository.SpeakersVariantRepository;
import org.andante.speakers.repository.entity.SpeakersVariantEntity;
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
public class DefaultSpeakersVariantService implements SpeakersVariantService {

    private static final String SPEAKERS_VARIANT_CONFLICT_EXCEPTION_MESSAGE = "Speakers variant with identifier %d already exists";
    private static final String SPEAKERS_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Speakers variant with identifier %d does not exist";

    private final SpeakersVariantRepository speakersVariantRepository;
    private final SpeakersVariantModelEntityMapper speakersVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<SpeakersVariantOutput> getAllById(Set<Long> identifiers) {
        List<SpeakersVariantEntity> databaseResponse = speakersVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(speakersVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<SpeakersVariantOutput> getAllBySpeakersId(Long speakersId) {
        Set<SpeakersVariantEntity> databaseResponse = speakersVariantRepository.findAllBySpeakersId(speakersId);

        return databaseResponse.stream()
                .map(speakersVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SpeakersVariantOutput create(SpeakersVariantInput speakersVariant) {
        if (speakersVariant.getId() != null && speakersVariantRepository.existsById(speakersVariant.getId())) {
            throw new SpeakersVariantConflictException(String.format(SPEAKERS_VARIANT_CONFLICT_EXCEPTION_MESSAGE, speakersVariant.getId()));
        }

        SpeakersVariantEntity speakersVariantEntity = speakersVariantModelEntityMapper.toEntity(speakersVariant);

        SpeakersVariantEntity createdVariant = speakersVariantRepository.save(speakersVariantEntity);

        return speakersVariantModelEntityMapper.toModel(createdVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SpeakersVariantOutput modify(SpeakersVariantInput speakersVariant) {
        if (speakersVariant.getId() == null || !speakersVariantRepository.existsById(speakersVariant.getId())) {
            throw new SpeakersVariantNotFoundException(String.format(SPEAKERS_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, speakersVariant.getId()));
        }

        SpeakersVariantEntity speakersVariantEntity = speakersVariantModelEntityMapper.toEntity(speakersVariant);

        SpeakersVariantEntity modifiedVariant = speakersVariantRepository.save(speakersVariantEntity);

        return speakersVariantModelEntityMapper.toModel(modifiedVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SpeakersVariantOutput delete(Long identifier) {
        Optional<SpeakersVariantEntity> databaseResponse = speakersVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new SpeakersVariantNotFoundException(String.format(SPEAKERS_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        speakersVariantRepository.delete(databaseResponse.get());

        return speakersVariantModelEntityMapper.toModel(databaseResponse.get());
    }
}
