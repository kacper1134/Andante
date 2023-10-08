package org.andante.headphones.controller.mapper;

import org.andante.headphones.dto.HeadphonesInputDTO;
import org.andante.headphones.dto.HeadphonesOutputDTO;
import org.andante.headphones.logic.model.HeadphonesInput;
import org.andante.headphones.logic.model.HeadphonesOutput;
import org.andante.product.enums.ProductType;
import org.springframework.stereotype.Component;

@Component
public class HeadphonesDTOModelMapper {
    public HeadphonesOutputDTO toDTO(HeadphonesOutput headphonesOutput) {
        return headphonesOutput.toDTO();
    }

    public HeadphonesInput toModel(HeadphonesInputDTO headphonesInputDTO) {
        return HeadphonesInput.builder()
                .id(headphonesInputDTO.getId())
                .name(headphonesInputDTO.getName())
                .description(headphonesInputDTO.getDescription())
                .weight(headphonesInputDTO.getWeight())
                .basePrice(headphonesInputDTO.getPrice())
                .minimumFrequency(headphonesInputDTO.getMinimumFrequency())
                .maximumFrequency(headphonesInputDTO.getMaximumFrequency())
                .productType(ProductType.HEADPHONES)
                .commentsIds(headphonesInputDTO.getCommentIds())
                .observers(headphonesInputDTO.getObservers())
                .producerName(headphonesInputDTO.getProducerName())
                .constructionType(headphonesInputDTO.getConstructionType())
                .driverType(headphonesInputDTO.getDriverType())
                .wireless(headphonesInputDTO.getWireless())
                .bluetoothStandard(headphonesInputDTO.getBluetoothStandard().orElse(null))
                .variantsIds(headphonesInputDTO.getVariantsIds())
                .build();
    }
}
