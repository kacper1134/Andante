package org.andante.amplifiers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.andante.product.logic.model.ProductVariantInput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AmplifiersVariantInput extends ProductVariantInput {

    private String color;
    private Long amplifiersId;

    @Override
    public AmplifiersVariantEntity toEntity() {
        return AmplifiersVariantEntity.builder()
                .id(getId())
                .priceDifference(getPriceDifference())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .build();
    }
}
