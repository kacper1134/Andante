package org.andante.gramophones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
import org.andante.product.logic.model.ProductVariantInput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class GramophonesVariantInput extends ProductVariantInput {

    private String color;
    private Long gramophonesId;

    @Override
    public GramophonesVariantEntity toEntity() {
        return GramophonesVariantEntity.builder()
                .id(getId())
                .priceDifference(getPriceDifference())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .build();
    }
}
