package org.andante.gramophones.controller.mapper;

import org.andante.gramophones.dto.GramophonesInputDTO;
import org.andante.gramophones.dto.GramophonesOutputDTO;
import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesOutput;
import org.andante.product.enums.ProductType;
import org.springframework.stereotype.Component;

@Component
public class GramophonesDTOModelMapper {

    public GramophonesOutputDTO toDTO(GramophonesOutput gramophonesOutput) {
        return gramophonesOutput.toDTO();
    }

    public GramophonesInput toModel(GramophonesInputDTO gramophonesDTO) {
        return GramophonesInput.builder()
                .id(gramophonesDTO.getId())
                .name(gramophonesDTO.getName())
                .description(gramophonesDTO.getDescription())
                .weight(gramophonesDTO.getWeight())
                .basePrice(gramophonesDTO.getPrice())
                .minimumFrequency(gramophonesDTO.getMinimumFrequency())
                .maximumFrequency(gramophonesDTO.getMaximumFrequency())
                .productType(ProductType.GRAMOPHONES)
                .commentsIds(gramophonesDTO.getCommentIds())
                .observers(gramophonesDTO.getObservers())
                .producerName(gramophonesDTO.getProducerName())
                .connectivityTechnology(gramophonesDTO.getConnectivityTechnology())
                .turntableMaterial(gramophonesDTO.getTurntableMaterial())
                .motorType(gramophonesDTO.getMotorType())
                .powerSource(gramophonesDTO.getPowerSource())
                .maximumFrequency(gramophonesDTO.getMaximumFrequency())
                .maximumRotationalSpeed(gramophonesDTO.getMaximumRotationalSpeed())
                .variantsIds(gramophonesDTO.getVariantsIds())
                .build();
    }
}
