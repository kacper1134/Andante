package org.andante.headphones.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.logic.model.HeadphonesVariantOutput;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;

import javax.persistence.*;

@Entity
@Table(name = "HeadphonesVariants")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HeadphonesVariantEntity extends ProductVariantEntity {

    @Column(nullable = false, name = "impedance")
    private Integer nominalImpedance;

    @Column(nullable = false, name = "max_loudness")
    private Integer loudness;

    @Column(nullable = false, name = "color", length = 50)
    private String color;

    @ManyToOne
    @JoinColumn(name = "headphones_id", nullable = false)
    private HeadphonesEntity headphones;

    @Override
    public ProductEntity getProduct() {
        return headphones;
    }

    @Override
    public void setProduct(ProductEntity product) {
        if (isProductHeadphones(product)) {
            this.headphones = (HeadphonesEntity) product;
        }
    }

    @Override
    public HeadphonesVariantOutput toModel() {
        return HeadphonesVariantOutput.builder()
                .id(getId())
                .price(headphones.getBasePrice().add(getPriceDifference()))
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .nominalImpedance(nominalImpedance)
                .loudness(loudness)
                .color(color)
                .headphonesId(headphones.getId())
                .observers(headphones.getObservers())
                .productName(headphones.getName())
                .build();
    }

    private boolean isProductHeadphones(ProductEntity product) {
        return product instanceof HeadphonesEntity;
    }
}
