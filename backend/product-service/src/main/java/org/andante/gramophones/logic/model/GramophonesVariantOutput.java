package org.andante.gramophones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.dto.GramophonesVariantOutputDTO;
import org.andante.product.logic.model.ProductVariantOutput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class GramophonesVariantOutput extends ProductVariantOutput {

    private String color;
    private Long gramophonesId;

    @Override
    public GramophonesVariantOutputDTO toDTO() {
        return GramophonesVariantOutputDTO.builder()
                .id(getId())
                .price(getPrice())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .gramophonesId(gramophonesId)
                .observers(getObservers())
                .productName(getProductName())
                .build();
    }
}
