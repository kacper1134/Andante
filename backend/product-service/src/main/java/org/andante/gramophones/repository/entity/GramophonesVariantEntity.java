package org.andante.gramophones.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "GramophonesVariants")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GramophonesVariantEntity extends ProductVariantEntity {

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "gramophone_id", nullable = false)
    private GramophonesEntity gramophones;


    @Override
    public ProductEntity getProduct() {
        return gramophones;
    }

    @Override
    public void setProduct(ProductEntity product) {
        if (isGramophone(product)) {
            this.gramophones = (GramophonesEntity) product;
        }
    }

    @Override
    public GramophonesVariantOutput toModel() {
        return GramophonesVariantOutput.builder()
                .id(getId())
                .price(gramophones.getBasePrice().add(getPriceDifference()))
                .availableQuantity(getAvailableQuantity())
                .imageUrl(getImageUrl())
                .thumbnailUrl(getThumbnailUrl())
                .color(getColor())
                .gramophonesId(getId())
                .observers(gramophones.getObservers())
                .productName(gramophones.getName())
                .build();
    }

    private boolean isGramophone(ProductEntity product) {
        return product instanceof GramophonesEntity;
    }
}
