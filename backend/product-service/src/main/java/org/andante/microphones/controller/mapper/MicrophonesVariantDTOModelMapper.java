package org.andante.microphones.controller.mapper;

import org.andante.microphones.dto.MicrophonesVariantInputDTO;
import org.andante.microphones.dto.MicrophonesVariantOutputDTO;
import org.andante.microphones.logic.model.MicrophonesVariantInput;
import org.andante.microphones.logic.model.MicrophonesVariantOutput;
import org.springframework.stereotype.Component;

@Component
public class MicrophonesVariantDTOModelMapper {

    public MicrophonesVariantOutputDTO toDTO(MicrophonesVariantOutput microphonesVariant) {
        return microphonesVariant.toDTO();
    }

    public MicrophonesVariantInput toModel(MicrophonesVariantInputDTO microphonesVariantDTO) {
        return MicrophonesVariantInput.builder()
                .id(microphonesVariantDTO.getId())
                .priceDifference(microphonesVariantDTO.getPriceDifference())
                .availableQuantity(microphonesVariantDTO.getAvailableQuantity())
                .imageUrl(microphonesVariantDTO.getImageUrl())
                .thumbnailUrl(microphonesVariantDTO.getThumbnailUrl())
                .color(microphonesVariantDTO.getColor())
                .microphoneId(microphonesVariantDTO.getMicrophoneId())
                .build();
    }
}
