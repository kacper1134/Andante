package org.andante.amplifiers.controller.mapper;

import org.andante.amplifiers.dto.AmplifiersInputDTO;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.product.enums.ProductType;
import org.springframework.stereotype.Component;

@Component
public class AmplifiersDTOModelMapper {

    public AmplifiersOutputDTO toDTO(AmplifiersOutput amplifiersOutput) {
        return amplifiersOutput.toDTO();
    }

    public AmplifiersInput toModel(AmplifiersInputDTO amplifiersInputDTO) {
        return AmplifiersInput.builder()
                .id(amplifiersInputDTO.getId())
                .name(amplifiersInputDTO.getName())
                .description(amplifiersInputDTO.getDescription())
                .weight(amplifiersInputDTO.getWeight())
                .basePrice(amplifiersInputDTO.getPrice())
                .minimumFrequency(amplifiersInputDTO.getMinimumFrequency())
                .maximumFrequency(amplifiersInputDTO.getMaximumFrequency())
                .productType(ProductType.AMPLIFIERS)
                .commentsIds(amplifiersInputDTO.getCommentIds())
                .observers(amplifiersInputDTO.getObservers())
                .producerName(amplifiersInputDTO.getProducerName())
                .power(amplifiersInputDTO.getPower())
                .amplifierType(amplifiersInputDTO.getAmplifierType())
                .variantsIds(amplifiersInputDTO.getVariantsIds())
                .build();
    }
}
