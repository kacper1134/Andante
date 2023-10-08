package org.andante.speakers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductVariantOutput;
import org.andante.speakers.dto.SpeakersVariantOutputDTO;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SpeakersVariantOutput extends ProductVariantOutput {

    private Integer loudness;
    private String color;
    private Long speakersId;

    @Override
    public SpeakersVariantOutputDTO toDTO() {
        return SpeakersVariantOutputDTO.builder()
                .id(getId())
                .price(getPrice())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .loudness(loudness)
                .color(color)
                .speakersId(speakersId)
                .observers(getObservers())
                .productName(getProductName())
                .build();
    }
}
