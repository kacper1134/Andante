package org.andante.microphones.controller.mapper;

import org.andante.microphones.dto.MicrophonesInputDTO;
import org.andante.microphones.dto.MicrophonesOutputDTO;
import org.andante.microphones.logic.model.MicrophonesInput;
import org.andante.microphones.logic.model.MicrophonesOutput;
import org.andante.product.enums.ProductType;
import org.springframework.stereotype.Component;

@Component
public class MicrophonesDTOModelMapper {

    public MicrophonesOutputDTO toDTO(MicrophonesOutput microphonesOutput) {
        return microphonesOutput.toDTO();
    }

    public MicrophonesInput toModel(MicrophonesInputDTO microphonesInputDTO) {
        return MicrophonesInput.builder()
                .id(microphonesInputDTO.getId())
                .name(microphonesInputDTO.getName())
                .description(microphonesInputDTO.getDescription())
                .weight(microphonesInputDTO.getWeight())
                .basePrice(microphonesInputDTO.getPrice())
                .minimumFrequency(microphonesInputDTO.getMinimumFrequency())
                .maximumFrequency(microphonesInputDTO.getMaximumFrequency())
                .productType(ProductType.MICROPHONES)
                .commentsIds(microphonesInputDTO.getCommentIds())
                .observers(microphonesInputDTO.getObservers())
                .producerName(microphonesInputDTO.getProducerName())
                .wireless(microphonesInputDTO.getWireless())
                .bluetoothStandard(microphonesInputDTO.getBluetoothStandard().orElse(null))
                .type(microphonesInputDTO.getType())
                .variantsIds(microphonesInputDTO.getVariantsIds())
                .build();
    }
}
