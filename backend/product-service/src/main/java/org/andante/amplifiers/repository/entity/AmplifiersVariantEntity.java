package org.andante.amplifiers.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;

import javax.persistence.*;

@Entity
@Table(name = "AmplifiersVariants")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmplifiersVariantEntity extends ProductVariantEntity {

    @Column(nullable = false, name = "color", length = 50)
    private String color;

    @ManyToOne
    @JoinColumn(name = "amplifiers_id", nullable = false)
    private AmplifiersEntity amplifiers;

    @Override
    public ProductEntity getProduct() {
        return amplifiers;
    }

    @Override
    public void setProduct(ProductEntity product) {
        if (isProductAmplifiers(product)) {
            this.amplifiers = (AmplifiersEntity) product;
        }
    }

    @Override
    public AmplifiersVariantOutput toModel() {
        return AmplifiersVariantOutput.builder()
                .id(getId())
                .price(amplifiers.getBasePrice().add(getPriceDifference()))
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .amplifiersId(amplifiers.getId())
                .observers(amplifiers.getObservers())
                .productName(amplifiers.getName())
                .build();
    }

    private boolean isProductAmplifiers(ProductEntity product) {
        return product instanceof AmplifiersEntity;
    }
}
