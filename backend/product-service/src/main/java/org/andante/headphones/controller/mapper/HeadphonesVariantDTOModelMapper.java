package org.andante.headphones.controller.mapper;

import org.andante.headphones.dto.HeadphonesVariantInputDTO;
import org.andante.headphones.dto.HeadphonesVariantOutputDTO;
import org.andante.headphones.logic.model.HeadphonesVariantInput;
import org.andante.headphones.logic.model.HeadphonesVariantOutput;
import org.springframework.stereotype.Component;

@Component
public class HeadphonesVariantDTOModelMapper {

    public HeadphonesVariantOutputDTO toDTO(HeadphonesVariantOutput headphonesVariant) {
        return headphonesVariant.toDTO();
    }

    public HeadphonesVariantInput toModel(HeadphonesVariantInputDTO headphonesVariantDTO) {
        return HeadphonesVariantInput.builder()
                .id(headphonesVariantDTO.getId())
                .priceDifference(headphonesVariantDTO.getPriceDifference())
                .availableQuantity(headphonesVariantDTO.getAvailableQuantity())
                .imageUrl(headphonesVariantDTO.getImageUrl())
                .thumbnailUrl(headphonesVariantDTO.getThumbnailUrl())
                .nominalImpedance(headphonesVariantDTO.getNominalImpedance())
                .loudness(headphonesVariantDTO.getLoudness())
                .color(headphonesVariantDTO.getColor())
                .headphonesId(headphonesVariantDTO.getHeadphonesId())
                .build();
    }
}
