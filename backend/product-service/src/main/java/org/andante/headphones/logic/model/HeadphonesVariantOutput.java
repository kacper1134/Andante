package org.andante.headphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.dto.HeadphonesVariantOutputDTO;
import org.andante.product.logic.model.ProductVariantOutput;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class HeadphonesVariantOutput extends ProductVariantOutput {

    private Integer nominalImpedance;
    private Integer loudness;
    private String color;
    private Long headphonesId;

    @Override
    public HeadphonesVariantOutputDTO toDTO() {
        return HeadphonesVariantOutputDTO.builder()
                .id(getId())
                .price(getPrice())
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .nominalImpedance(nominalImpedance)
                .loudness(loudness)
                .color(color)
                .headphonesId(headphonesId)
                .observers(getObservers())
                .productName(getProductName())
                .build();
    }
}
