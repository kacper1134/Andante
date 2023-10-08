package org.andante.subwoofers.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.andante.subwoofers.logic.model.SubwoofersVariantOutput;

import javax.persistence.*;

@Entity
@Table(name = "SubwoofersVariants")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubwoofersVariantEntity extends ProductVariantEntity {

    @Column(nullable = false, name = "color", length = 50)
    private String color;

    @ManyToOne
    @JoinColumn(name = "subwoofers_id", nullable = false)
    private SubwoofersEntity subwoofers;

    @Override
    public ProductEntity getProduct() {
        return subwoofers;
    }

    @Override
    public void setProduct(ProductEntity product) {
        if (isProductSubwoofers(product)) {
            this.subwoofers = (SubwoofersEntity) product;
        }
    }

    @Override
    public SubwoofersVariantOutput toModel() {
        return SubwoofersVariantOutput.builder()
                .id(getId())
                .price(subwoofers.getBasePrice().add(getPriceDifference()))
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(color)
                .subwoofersId(subwoofers.getId())
                .observers(subwoofers.getObservers())
                .productName(subwoofers.getName())
                .build();
    }

    private boolean isProductSubwoofers(ProductEntity product) {
        return product instanceof SubwoofersEntity;
    }
}
