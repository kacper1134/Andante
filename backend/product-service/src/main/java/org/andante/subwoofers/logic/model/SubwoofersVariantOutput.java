package org.andante.subwoofers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.ProductVariantOutput;
import org.andante.subwoofers.dto.SubwoofersVariantOutputDTO;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SubwoofersVariantOutput extends ProductVariantOutput {

    private String color;
    private Long subwoofersId;

    @Override
    public SubwoofersVariantOutputDTO toDTO() {
        return SubwoofersVariantOutputDTO.builder()
                .id(getId())
                .price(getPrice())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .subwoofersId(subwoofersId)
                .observers(getObservers())
                .productName(getProductName())
                .build();
    }
}
