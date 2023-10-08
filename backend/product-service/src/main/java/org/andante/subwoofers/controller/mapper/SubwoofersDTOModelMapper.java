package org.andante.subwoofers.controller.mapper;

import org.andante.product.enums.ProductType;
import org.andante.subwoofers.dto.SubwoofersInputDTO;
import org.andante.subwoofers.dto.SubwoofersOutputDTO;
import org.andante.subwoofers.logic.model.SubwoofersInput;
import org.andante.subwoofers.logic.model.SubwoofersOutput;
import org.springframework.stereotype.Component;

@Component
public class SubwoofersDTOModelMapper {

    public SubwoofersOutputDTO toDTO(SubwoofersOutput subwoofersOutput) {
        return subwoofersOutput.toDTO();
    }

    public SubwoofersInput toModel(SubwoofersInputDTO subwoofersInputDTO) {
        return SubwoofersInput.builder()
                .id(subwoofersInputDTO.getId())
                .name(subwoofersInputDTO.getName())
                .description(subwoofersInputDTO.getDescription())
                .weight(subwoofersInputDTO.getWeight())
                .basePrice(subwoofersInputDTO.getPrice())
                .minimumFrequency(subwoofersInputDTO.getMinimumFrequency())
                .maximumFrequency(subwoofersInputDTO.getMaximumFrequency())
                .productType(ProductType.SUBWOOFERS)
                .commentsIds(subwoofersInputDTO.getCommentIds())
                .observers(subwoofersInputDTO.getObservers())
                .producerName(subwoofersInputDTO.getProducerName())
                .power(subwoofersInputDTO.getPower())
                .type(subwoofersInputDTO.getType())
                .variantsIds(subwoofersInputDTO.getVariantsIds())
                .build();
    }
}
