package org.andante.speakers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductVariantInput;
import org.andante.speakers.repository.entity.SpeakersVariantEntity;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SpeakersVariantInput extends ProductVariantInput {

    private Integer loudness;
    private String color;
    private Long speakersId;

    @Override
    public SpeakersVariantEntity toEntity() {
        return SpeakersVariantEntity.builder()
                .id(getId())
                .priceDifference(getPriceDifference())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .loudness(loudness)
                .color(color)
                .build();
    }
}
