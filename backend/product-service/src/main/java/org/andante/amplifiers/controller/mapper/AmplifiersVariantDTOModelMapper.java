package org.andante.amplifiers.controller.mapper;

import org.andante.amplifiers.dto.AmplifiersVariantInputDTO;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
import org.springframework.stereotype.Component;

@Component
public class AmplifiersVariantDTOModelMapper {

    public AmplifiersVariantOutputDTO toDTO(AmplifiersVariantOutput amplifiersVariant) {
        return amplifiersVariant.toDTO();
    }

    public AmplifiersVariantInput toModel(AmplifiersVariantInputDTO amplifiersVariantDTO) {
        return AmplifiersVariantInput.builder()
                .id(amplifiersVariantDTO.getId())
                .priceDifference(amplifiersVariantDTO.getPriceDifference())
                .availableQuantity(amplifiersVariantDTO.getAvailableQuantity())
                .imageUrl(amplifiersVariantDTO.getImageUrl())
                .thumbnailUrl(amplifiersVariantDTO.getThumbnailUrl())
                .color(amplifiersVariantDTO.getColor())
                .amplifiersId(amplifiersVariantDTO.getAmplifiersId())
                .build();
    }
}
