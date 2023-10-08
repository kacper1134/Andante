package org.andante.amplifiers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.product.logic.model.ProductVariantOutput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AmplifiersVariantOutput extends ProductVariantOutput {

    private String color;
    private Long amplifiersId;

    @Override
    public AmplifiersVariantOutputDTO toDTO() {
        return AmplifiersVariantOutputDTO.builder()
                .id(getId())
                .price(getPrice())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .amplifiersId(amplifiersId)
                .observers(getObservers())
                .productName(getProductName())
                .build();
    }
}
