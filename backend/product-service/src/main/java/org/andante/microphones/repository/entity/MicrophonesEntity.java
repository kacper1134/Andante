package org.andante.microphones.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.enums.MicrophoneType;
import org.andante.microphones.logic.model.MicrophonesOutput;
import org.andante.product.enums.ProductType;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Microphones")
public class MicrophonesEntity extends ProductEntity {

    @Column(nullable = false)
    private Boolean wireless;

    @Column(name = "bluetooth")
    private Float bluetoothStandard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private MicrophoneType type;

    @OneToMany(mappedBy="microphones", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MicrophonesVariantEntity> variants;

    @Override
    public Set<ProductVariantEntity> getVariants() {
        return new HashSet<>(variants);
    }

    @Override
    public void setVariants(Set<ProductVariantEntity> variants) {
        if (areAllVariantsMicrophones(variants)) {
            this.variants = variants.stream()
                    .map(MicrophonesVariantEntity.class::cast)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public MicrophonesOutput toModel() {
        return MicrophonesOutput.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .weight(getWeight())
                .basePrice(getBasePrice())
                .minimumFrequency(getMinimumFrequency())
                .maximumFrequency(getMaximumFrequency())
                .productType(ProductType.AMPLIFIERS)
                .creationTimestamp(getCreationTimestamp())
                .modificationTimestamp(getModificationTimestamp())
                .comments(getComments().stream()
                        .map(CommentEntity::toModel)
                        .sorted(Comparator.comparing(Comment::getCreationTimestamp).reversed())
                        .collect(Collectors.toList()))
                .observers(getObservers())
                .producer(getProducer().toModel())
                .wireless(wireless)
                .bluetoothStandard(bluetoothStandard)
                .type(type)
                .variants(variants.stream()
                        .map(MicrophonesVariantEntity::toModel)
                        .collect(Collectors.toSet()))
                .build();
    }

    private boolean areAllVariantsMicrophones(Set<ProductVariantEntity> variants) {
        return variants.stream()
                .allMatch(MicrophonesVariantEntity.class::isInstance);
    }
}
