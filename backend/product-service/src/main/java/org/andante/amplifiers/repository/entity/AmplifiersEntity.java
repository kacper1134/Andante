package org.andante.amplifiers.repository.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.enums.AmplifierType;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.product.enums.ProductType;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Amplifiers")
@EqualsAndHashCode(callSuper = true)
public class AmplifiersEntity extends ProductEntity {

    @Column(nullable = false)
    private Float power;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private AmplifierType type;

    @OneToMany(mappedBy="amplifiers", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AmplifiersVariantEntity> variants;

    @Override
    public List<ProductVariantEntity> getVariants() {
        return new ArrayList<>(Optional.ofNullable(variants)
                .orElse(List.of()));
    }

    @Override
    public void setVariants(Set<ProductVariantEntity> variants) {
        if (areAllVariantsAmplifiers(variants)) {
            this.variants = variants.stream()
                    .map(AmplifiersVariantEntity.class::cast)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public AmplifiersOutput toModel() {
        return AmplifiersOutput.builder()
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
                .amplifierType(type)
                .variants(variants.stream()
                        .map(AmplifiersVariantEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }

    private boolean areAllVariantsAmplifiers(Set<ProductVariantEntity> variants) {
        return variants.stream()
                .allMatch(AmplifiersVariantEntity.class::isInstance);
    }
}
