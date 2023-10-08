package org.andante.speakers.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.speakers.exception.SpeakersNotFoundException;
import org.andante.speakers.logic.model.SpeakersVariantInput;
import org.andante.speakers.logic.model.SpeakersVariantOutput;
import org.andante.speakers.repository.SpeakersRepository;
import org.andante.speakers.repository.entity.SpeakersEntity;
import org.andante.speakers.repository.entity.SpeakersVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SpeakersVariantModelEntityMapper {

    private static final String SPEAKERS_NOT_FOUND_EXCEPTION_MESSAGE = "Speakers with identifier %d do not exist";

    private final SpeakersRepository speakersRepository;

    public SpeakersVariantOutput toModel(SpeakersVariantEntity speakersVariantEntity) {
        return speakersVariantEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SpeakersVariantEntity toEntity(SpeakersVariantInput speakersVariant) {
        SpeakersEntity speakersEntity = speakersRepository.findById(speakersVariant.getSpeakersId())
                .orElseThrow(() -> new SpeakersNotFoundException(String.format(SPEAKERS_NOT_FOUND_EXCEPTION_MESSAGE, speakersVariant.getSpeakersId())));

        return SpeakersVariantEntity.builder()
                .id(speakersVariant.getId())
                .priceDifference(speakersVariant.getPriceDifference())
                .availableQuantity(speakersVariant.getAvailableQuantity())
                .imageUrl(speakersVariant.getImageUrl())
                .thumbnailUrl(speakersVariant.getThumbnailUrl())
                .loudness(speakersVariant.getLoudness())
                .color(speakersVariant.getColor())
                .speakers(speakersEntity)
                .build();
    }
}
