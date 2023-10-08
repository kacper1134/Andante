package org.andante.subwoofers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductVariantInput;
import org.andante.subwoofers.repository.entity.SubwoofersVariantEntity;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SubwoofersVariantInput extends ProductVariantInput {

    private String color;
    private Long subwoofersId;

    @Override
    public SubwoofersVariantEntity toEntity() {
        return SubwoofersVariantEntity.builder()
                .id(getId())
                .priceDifference(getPriceDifference())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .build();
    }
}
