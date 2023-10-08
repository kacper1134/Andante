package org.andante.speakers.controller.mappers;

import org.andante.speakers.dto.SpeakersVariantInputDTO;
import org.andante.speakers.dto.SpeakersVariantOutputDTO;
import org.andante.speakers.logic.model.SpeakersVariantInput;
import org.andante.speakers.logic.model.SpeakersVariantOutput;
import org.springframework.stereotype.Component;

@Component
public class SpeakersVariantDTOModelMapper {

    public SpeakersVariantOutputDTO toDTO(SpeakersVariantOutput speakersVariant) {
        return speakersVariant.toDTO();
    }

    public SpeakersVariantInput toModel(SpeakersVariantInputDTO speakersVariantDTO) {
        return SpeakersVariantInput.builder()
                .id(speakersVariantDTO.getId())
                .priceDifference(speakersVariantDTO.getPriceDifference())
                .availableQuantity(speakersVariantDTO.getAvailableQuantity())
                .imageUrl(speakersVariantDTO.getImageUrl())
                .thumbnailUrl(speakersVariantDTO.getThumbnailUrl())
                .loudness(speakersVariantDTO.getLoudness())
                .color(speakersVariantDTO.getColor())
                .speakersId(speakersVariantDTO.getSpeakersId())
                .build();
    }
}
