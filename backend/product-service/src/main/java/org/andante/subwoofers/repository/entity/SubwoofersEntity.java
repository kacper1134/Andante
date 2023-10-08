package org.andante.subwoofers.repository.entity;

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
import org.andante.subwoofers.enums.SubwooferType;
import org.andante.subwoofers.logic.model.SubwoofersOutput;

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
@Table(name = "Subwoofers")
public class SubwoofersEntity extends ProductEntity {

    @Column(nullable = false)
    private Float power;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private SubwooferType type;

    @OneToMany(mappedBy="subwoofers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SubwoofersVariantEntity> variants;

    @Override
    public Set<ProductVariantEntity> getVariants() {
        return new HashSet<>(variants);
    }

    @Override
    public void setVariants(Set<ProductVariantEntity> variants) {
        if (areAllVariantsSubwoofers((variants))) {
            this.variants = variants.stream()
                    .map(SubwoofersVariantEntity.class::cast)
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public SubwoofersOutput toModel() {
        return SubwoofersOutput.builder()
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
                .power(power)
                .type(type)
                .variants(variants.stream()
                        .map(SubwoofersVariantEntity::toModel)
                        .collect(Collectors.toSet()))
                .build();
    }

    private boolean areAllVariantsSubwoofers(Set<ProductVariantEntity> variants) {
        return variants.stream()
                .allMatch(SubwoofersVariantEntity.class::isInstance);
    }
}
