package org.andante.headphones.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.enums.ConstructionType;
import org.andante.headphones.enums.DriverType;
import org.andante.headphones.logic.model.HeadphonesOutput;
import org.andante.product.enums.ProductType;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Headphones")
public class HeadphonesEntity extends ProductEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "construction_type")
    private ConstructionType constructionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "driver_type")
    private DriverType driverType;

    @Column(nullable = false)
    private Boolean wireless;

    @Column(name = "bluetooth")
    private Float bluetoothStandard;

    @OneToMany(mappedBy="headphones", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HeadphonesVariantEntity> variants;

    @Override
    public List<ProductVariantEntity> getVariants() {
        return new ArrayList<>(variants);
    }

    @Override
    public void setVariants(Set<ProductVariantEntity> variants) {
        if (areAllVariantsHeadphones(variants)) {
            this.variants = variants.stream()
                    .map(HeadphonesVariantEntity.class::cast)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public HeadphonesOutput toModel() {
        return HeadphonesOutput.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .basePrice(getBasePrice())
                .weight(getWeight())
                .basePrice(getBasePrice())
                .minimumFrequency(getMinimumFrequency())
                .maximumFrequency(getMaximumFrequency())
                .productType(ProductType.HEADPHONES)
                .creationTimestamp(getCreationTimestamp())
                .modificationTimestamp(getModificationTimestamp())
                .comments(getComments().stream()
                        .map(CommentEntity::toModel)
                        .sorted(Comparator.comparing(Comment::getCreationTimestamp).reversed())
                        .collect(Collectors.toList()))
                .observers(getObservers())
                .producer(getProducer().toModel())
                .constructionType(constructionType)
                .driverType(driverType)
                .wireless(wireless)
                .bluetoothStandard(bluetoothStandard)
                .variants(variants.stream()
                        .map(HeadphonesVariantEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }

    private boolean areAllVariantsHeadphones(Set<ProductVariantEntity> variants) {
        return variants.stream()
                .allMatch(HeadphonesVariantEntity.class::isInstance);
    }
}
