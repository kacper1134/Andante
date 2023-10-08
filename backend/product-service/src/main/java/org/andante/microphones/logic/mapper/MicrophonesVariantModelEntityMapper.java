package org.andante.microphones.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.microphones.exception.MicrophonesNotFoundException;
import org.andante.microphones.logic.model.MicrophonesVariantInput;
import org.andante.microphones.logic.model.MicrophonesVariantOutput;
import org.andante.microphones.repository.MicrophonesRepository;
import org.andante.microphones.repository.entity.MicrophonesEntity;
import org.andante.microphones.repository.entity.MicrophonesVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MicrophonesVariantModelEntityMapper {

    private static final String MICROPHONES_NOT_FOUND_EXCEPTION_MESSAGE = "Microphones with identifier %d do not exist";

    private final MicrophonesRepository microphonesRepository;

    public MicrophonesVariantOutput toModel(MicrophonesVariantEntity microphonesVariantEntity) {
        return microphonesVariantEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public MicrophonesVariantEntity toEntity(MicrophonesVariantInput microphonesVariant) {
        MicrophonesEntity microphonesEntity = microphonesRepository.findById(microphonesVariant.getMicrophoneId())
                .orElseThrow(() -> new MicrophonesNotFoundException(String.format(MICROPHONES_NOT_FOUND_EXCEPTION_MESSAGE, microphonesVariant.getMicrophoneId())));

        return MicrophonesVariantEntity.builder()
                .id(microphonesVariant.getId())
                .priceDifference(microphonesVariant.getPriceDifference())
                .availableQuantity(microphonesVariant.getAvailableQuantity())
                .imageUrl(microphonesVariant.getImageUrl())
                .thumbnailUrl(microphonesVariant.getThumbnailUrl())
                .color(microphonesVariant.getColor())
                .microphones(microphonesEntity)
                .build();
    }
}
