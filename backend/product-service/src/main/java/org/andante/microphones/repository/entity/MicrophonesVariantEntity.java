package org.andante.microphones.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.logic.model.MicrophonesVariantOutput;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;

import javax.persistence.*;

@Entity
@Table(name = "MicrophonesVariants")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MicrophonesVariantEntity extends ProductVariantEntity {

    @Column(nullable = false, name = "color", length = 50)
    private String color;

    @ManyToOne
    @JoinColumn(name = "microphones_id", nullable = false)
    private MicrophonesEntity microphones;

    @Override
    public ProductEntity getProduct() {
        return microphones;
    }

    @Override
    public void setProduct(ProductEntity product) {
        if (isProductMicrophones(product)) {
            this.microphones = (MicrophonesEntity) product;
        }
    }

    @Override
    public MicrophonesVariantOutput toModel() {
        return MicrophonesVariantOutput.builder()
                .id(getId())
                .price(microphones.getBasePrice().add(getPriceDifference()))
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .microphoneId(microphones.getId())
                .observers(microphones.getObservers())
                .productName(microphones.getName())
                .build();
    }

    private boolean isProductMicrophones(ProductEntity product) {
        return product instanceof MicrophonesEntity;
    }
}
