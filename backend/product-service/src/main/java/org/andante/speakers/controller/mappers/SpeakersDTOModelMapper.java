package org.andante.speakers.controller.mappers;

import org.andante.product.enums.ProductType;
import org.andante.speakers.dto.SpeakersInputDTO;
import org.andante.speakers.dto.SpeakersOutputDTO;
import org.andante.speakers.logic.model.SpeakersInput;
import org.andante.speakers.logic.model.SpeakersOutput;
import org.springframework.stereotype.Component;

@Component
public class SpeakersDTOModelMapper {

    public SpeakersOutputDTO toDTO(SpeakersOutput speakersOutput) {
        return speakersOutput.toDTO();
    }

    public SpeakersInput toModel(SpeakersInputDTO speakersInputDTO) {
        return SpeakersInput.builder()
                .id(speakersInputDTO.getId())
                .name(speakersInputDTO.getName())
                .description(speakersInputDTO.getDescription())
                .weight(speakersInputDTO.getWeight())
                .basePrice(speakersInputDTO.getPrice())
                .minimumFrequency(speakersInputDTO.getMinimumFrequency())
                .maximumFrequency(speakersInputDTO.getMaximumFrequency())
                .productType(ProductType.SPEAKERS)
                .commentsIds(speakersInputDTO.getCommentIds())
                .observers(speakersInputDTO.getObservers())
                .producerName(speakersInputDTO.getProducerName())
                .wireless(speakersInputDTO.getWireless())
                .bluetoothStandard(speakersInputDTO.getBluetoothStandard().orElse(null))
                .variantsIds(speakersInputDTO.getVariantsIds())
                .build();
    }
}
