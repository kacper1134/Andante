package org.andante.headphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.repository.entity.HeadphonesVariantEntity;
import org.andante.product.logic.model.ProductVariantInput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class HeadphonesVariantInput extends ProductVariantInput {

    private Integer nominalImpedance;
    private Integer loudness;
    private String color;
    private Long headphonesId;

    @Override
    public HeadphonesVariantEntity toEntity() {
        return HeadphonesVariantEntity.builder()
                .id(getId())
                .priceDifference(getPriceDifference())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .nominalImpedance(nominalImpedance)
                .loudness(loudness)
                .color(color)
                .build();
    }
}
