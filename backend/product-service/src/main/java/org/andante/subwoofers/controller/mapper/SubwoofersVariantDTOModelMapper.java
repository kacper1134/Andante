package org.andante.subwoofers.controller.mapper;

import org.andante.subwoofers.dto.SubwoofersVariantInputDTO;
import org.andante.subwoofers.dto.SubwoofersVariantOutputDTO;
import org.andante.subwoofers.logic.model.SubwoofersVariantInput;
import org.andante.subwoofers.logic.model.SubwoofersVariantOutput;
import org.springframework.stereotype.Component;

@Component
public class SubwoofersVariantDTOModelMapper {

    public SubwoofersVariantOutputDTO toDTO(SubwoofersVariantOutput subwoofersVariant) {
        return subwoofersVariant.toDTO();
    }

    public SubwoofersVariantInput toModel(SubwoofersVariantInputDTO subwoofersVariantDTO) {
        return SubwoofersVariantInput.builder()
                .id(subwoofersVariantDTO.getId())
                .priceDifference(subwoofersVariantDTO.getPriceDifference())
                .availableQuantity(subwoofersVariantDTO.getAvailableQuantity())
                .imageUrl(subwoofersVariantDTO.getImageUrl())
                .thumbnailUrl(subwoofersVariantDTO.getThumbnailUrl())
                .color(subwoofersVariantDTO.getColor())
                .subwoofersId(subwoofersVariantDTO.getSubwoofersId())
                .build();
    }
}
