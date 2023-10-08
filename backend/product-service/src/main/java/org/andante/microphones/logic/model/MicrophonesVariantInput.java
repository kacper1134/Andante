package org.andante.microphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.repository.entity.MicrophonesVariantEntity;
import org.andante.product.logic.model.ProductVariantInput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class MicrophonesVariantInput extends ProductVariantInput {

    private String color;
    private Long microphoneId;

    @Override
    public MicrophonesVariantEntity toEntity() {
        return MicrophonesVariantEntity.builder()
                .id(getId())
                .priceDifference(getPriceDifference())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .build();
    }
}
