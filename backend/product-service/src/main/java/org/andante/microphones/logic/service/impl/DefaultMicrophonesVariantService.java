package org.andante.microphones.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.microphones.exception.MicrophonesVariantConflictException;
import org.andante.microphones.exception.MicrophonesVariantNotFoundException;
import org.andante.microphones.logic.mapper.MicrophonesVariantModelEntityMapper;
import org.andante.microphones.logic.model.MicrophonesVariantInput;
import org.andante.microphones.logic.model.MicrophonesVariantOutput;
import org.andante.microphones.logic.service.MicrophonesVariantService;
import org.andante.microphones.repository.MicrophonesVariantRepository;
import org.andante.microphones.repository.entity.MicrophonesVariantEntity;
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
public class DefaultMicrophonesVariantService implements MicrophonesVariantService {

    private static final String MICROPHONE_VARIANT_CONFLICT_EXCEPTION_MESSAGE = "Microphones variant with identifier %d already exists";
    private static final String MICROPHONE_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Microphones variant with identifier %d does not exist";

    private final MicrophonesVariantRepository microphonesVariantRepository;
    private final MicrophonesVariantModelEntityMapper microphonesVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<MicrophonesVariantOutput> getAllById(Set<Long> identifiers) {
        List<MicrophonesVariantEntity> databaseResponse = microphonesVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(microphonesVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<MicrophonesVariantOutput> getAllByMicrophoneId(Long microphoneId) {
        Set<MicrophonesVariantEntity> databaseResponse = microphonesVariantRepository.findAllByMicrophonesId(microphoneId);

        return databaseResponse.stream()
                .map(microphonesVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MicrophonesVariantOutput create(MicrophonesVariantInput microphonesVariant) {
        if (microphonesVariant.getId() != null && microphonesVariantRepository.existsById(microphonesVariant.getId())) {
            throw new MicrophonesVariantConflictException(String.format(MICROPHONE_VARIANT_CONFLICT_EXCEPTION_MESSAGE, microphonesVariant.getId()));
        }

        MicrophonesVariantEntity microphonesVariantEntity = microphonesVariantModelEntityMapper.toEntity(microphonesVariant);
        MicrophonesVariantEntity createdVariant = microphonesVariantRepository.save(microphonesVariantEntity);

        return microphonesVariantModelEntityMapper.toModel(createdVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MicrophonesVariantOutput modify(MicrophonesVariantInput microphonesVariant) {
        if (microphonesVariant.getId() == null || !microphonesVariantRepository.existsById(microphonesVariant.getId())) {
            throw new MicrophonesVariantNotFoundException(String.format(MICROPHONE_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, microphonesVariant.getId()));
        }

        MicrophonesVariantEntity microphonesVariantEntity = microphonesVariantModelEntityMapper.toEntity(microphonesVariant);

        MicrophonesVariantEntity updatedVariant = microphonesVariantRepository.save(microphonesVariantEntity);

        return microphonesVariantModelEntityMapper.toModel(updatedVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MicrophonesVariantOutput delete(Long identifier) {
        Optional<MicrophonesVariantEntity> databaseResponse = microphonesVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new MicrophonesVariantNotFoundException(String.format(MICROPHONE_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        microphonesVariantRepository.delete(databaseResponse.get());

        return microphonesVariantModelEntityMapper.toModel(databaseResponse.get());
    }
}
