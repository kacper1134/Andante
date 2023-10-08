package org.andante.speakers.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.andante.speakers.logic.model.SpeakersVariantOutput;

import javax.persistence.*;

@Entity
@Table(name = "SpeakersVariants")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpeakersVariantEntity extends ProductVariantEntity {

    @Column(nullable = false, name = "max_loudness")
    private Integer loudness;

    @Column(nullable = false, name = "color", length = 50)
    private String color;

    @ManyToOne
    @JoinColumn(name = "speakers_id", nullable = false)
    private SpeakersEntity speakers;

    @Override
    public ProductEntity getProduct() {
        return speakers;
    }

    @Override
    public void setProduct(ProductEntity product) {
        if (isProductSpeakers(product)) {
            this.speakers = (SpeakersEntity) product;
        }
    }

    @Override
    public SpeakersVariantOutput toModel() {
        return SpeakersVariantOutput.builder()
                .id(getId())
                .price(speakers.getBasePrice().add(getPriceDifference()))
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .loudness(loudness)
                .color(color)
                .speakersId(speakers.getId())
                .observers(speakers.getObservers())
                .productName(speakers.getName())
                .build();
    }

    private boolean isProductSpeakers(ProductEntity product) {
        return product instanceof SpeakersEntity;
    }
}
