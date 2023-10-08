package org.andante.microphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.dto.MicrophonesVariantOutputDTO;
import org.andante.product.logic.model.ProductVariantOutput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class MicrophonesVariantOutput extends ProductVariantOutput {

    private String color;
    private Long microphoneId;

    @Override
    public MicrophonesVariantOutputDTO toDTO() {
        return MicrophonesVariantOutputDTO.builder()
                .id(getId())
                .price(getPrice())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .microphoneId(microphoneId)
                .observers(getObservers())
                .productName(getProductName())
                .build();
    }
}
