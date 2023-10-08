package org.andante.gramophones.controller.mapper;

import org.andante.gramophones.dto.GramophonesVariantInputDTO;
import org.andante.gramophones.dto.GramophonesVariantOutputDTO;
import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;
import org.springframework.stereotype.Component;

@Component
public class GramophonesVariantDTOModelMapper {

    public GramophonesVariantOutputDTO toDTO(GramophonesVariantOutput gramophonesVariant) {
        return gramophonesVariant.toDTO();
    }

    public GramophonesVariantInput toModel(GramophonesVariantInputDTO gramophonesVariantDTO) {
        return GramophonesVariantInput.builder()
                .id(gramophonesVariantDTO.getId())
                .priceDifference(gramophonesVariantDTO.getPriceDifference())
                .availableQuantity(gramophonesVariantDTO.getAvailableQuantity())
                .imageUrl(gramophonesVariantDTO.getImageUrl())
                .thumbnailUrl(gramophonesVariantDTO.getThumbnailUrl())
                .color(gramophonesVariantDTO.getColor())
                .gramophonesId(gramophonesVariantDTO.getGramophonesId())
                .build();
    }
}
