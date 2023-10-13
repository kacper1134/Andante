package org.andante.speakers.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.product.enums.ProductType;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.andante.speakers.logic.model.SpeakersOutput;

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
@Table(name = "Speakers")
public class SpeakersEntity extends ProductEntity {

    @Column(nullable = false)
    private Boolean wireless;

    @Column(name = "bluetooth")
    private Float bluetoothStandard;

    @OneToMany(mappedBy="speakers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpeakersVariantEntity> variants;

    @Override
    public List<ProductVariantEntity> getVariants() {
        return new ArrayList<>(variants);
    }

    @Override
    public void setVariants(Set<ProductVariantEntity> variants) {
        if (areAllVariantsSpeakers(variants)) {
            this.variants = variants.stream()
                    .map(SpeakersVariantEntity.class::cast)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public SpeakersOutput toModel() {
        return SpeakersOutput.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .weight(getWeight())
                .basePrice(getBasePrice())
                .minimumFrequency(getMinimumFrequency())
                .maximumFrequency(getMaximumFrequency())
                .productType(ProductType.SPEAKERS)
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
                .variants(variants.stream()
                        .map(SpeakersVariantEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }

    private boolean areAllVariantsSpeakers(Set<ProductVariantEntity> variants) {
       return variants.stream()
               .allMatch(SpeakersVariantEntity.class::isInstance);
    }
}
