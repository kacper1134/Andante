package org.andante.amplifiers.logic.service.impl;

import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.exception.AmplifierVariantConflictException;
import org.andante.amplifiers.exception.AmplifierVariantNotFoundException;
import org.andante.amplifiers.logic.mapper.AmplifiersVariantModelEntityMapper;
import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
import org.andante.amplifiers.logic.service.AmplifiersVariantService;
import org.andante.amplifiers.repository.AmplifiersVariantRepository;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
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
public class DefaultAmplifiersVariantService implements AmplifiersVariantService {

    private static final String AMPLIFIER_VARIANT_CONFLICT_EXCEPTION_MESSAGE = "Amplifiers variant with identifier %d already exists";
    private static final String AMPLIFIER_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Amplifiers variant with identifier %d does not exist";

    private final AmplifiersVariantRepository amplifiersVariantRepository;
    private final AmplifiersVariantModelEntityMapper amplifiersVariantModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<AmplifiersVariantOutput> getAllById(Set<Long> identifiers) {
        List<AmplifiersVariantEntity> databaseResponse = amplifiersVariantRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(amplifiersVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<AmplifiersVariantOutput> getAllByProductId(Long productIdentifier) {
        Set<AmplifiersVariantEntity> databaseResponse = amplifiersVariantRepository.findAllByAmplifiersId(productIdentifier);

        return databaseResponse.stream()
                .map(amplifiersVariantModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AmplifiersVariantOutput create(AmplifiersVariantInput amplifiersVariant) {
        if (amplifiersVariant.getId() != null && amplifiersVariantRepository.existsById(amplifiersVariant.getId())) {
            throw new AmplifierVariantConflictException(String.format(AMPLIFIER_VARIANT_CONFLICT_EXCEPTION_MESSAGE, amplifiersVariant.getId()));
        }

        AmplifiersVariantEntity amplifiersVariantToCreate = amplifiersVariantModelEntityMapper.toEntity(amplifiersVariant);
        AmplifiersVariantEntity createdAmplifiersVariant = amplifiersVariantRepository.save(amplifiersVariantToCreate);

        return amplifiersVariantModelEntityMapper.toModel(createdAmplifiersVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AmplifiersVariantOutput update(AmplifiersVariantInput amplifiersVariant) {
        if (amplifiersVariant.getId() == null || !amplifiersVariantRepository.existsById(amplifiersVariant.getId())) {
            throw new AmplifierVariantNotFoundException(String.format(AMPLIFIER_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, amplifiersVariant.getId()));
        }

        AmplifiersVariantEntity amplifiersVariantToModify = amplifiersVariantModelEntityMapper.toEntity(amplifiersVariant);

        AmplifiersVariantEntity modifiedAmplifiersVariant = amplifiersVariantRepository.save(amplifiersVariantToModify);

        return amplifiersVariantModelEntityMapper.toModel(modifiedAmplifiersVariant);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AmplifiersVariantOutput delete(Long identifier) {
        Optional<AmplifiersVariantEntity> databaseResponse = amplifiersVariantRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new AmplifierVariantNotFoundException(String.format(AMPLIFIER_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        amplifiersVariantRepository.delete(databaseResponse.get());

        return amplifiersVariantModelEntityMapper.toModel(databaseResponse.get());
    }
}
