package org.andante.gramophones.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.enums.ConnectivityTechnology;
import org.andante.gramophones.enums.MotorType;
import org.andante.gramophones.enums.PowerSource;
import org.andante.gramophones.enums.TurntableMaterial;
import org.andante.gramophones.logic.model.GramophonesOutput;
import org.andante.product.enums.ProductType;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Gramophones")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class GramophonesEntity extends ProductEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "conn_tech")
    private ConnectivityTechnology connectivityTechnology;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "table_material")
    private TurntableMaterial turntableMaterial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "motor_type")
    private MotorType motorType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "power_source")
    private PowerSource powerSource;

    @Column(nullable = false, name = "rotation_speed")
    private Integer maximumRotationalSpeed;

    @OneToMany(mappedBy = "gramophones", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GramophonesVariantEntity> variants;

    @Override
    public List<ProductVariantEntity> getVariants() {
        return new ArrayList<>(variants);
    }

    @Override
    public void setVariants(Set<ProductVariantEntity> variants) {
        if (allAreVariantsGramophones(variants)) {
            this.variants = variants.stream()
                    .map(GramophonesVariantEntity.class::cast)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public GramophonesOutput toModel() {
        return GramophonesOutput.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .weight(getWeight())
                .basePrice(getBasePrice())
                .minimumFrequency(getMinimumFrequency())
                .maximumFrequency(getMaximumFrequency())
                .productType(ProductType.GRAMOPHONES)
                .creationTimestamp(getCreationTimestamp())
                .modificationTimestamp(getModificationTimestamp())
                .comments(getComments().stream()
                        .map(CommentEntity::toModel)
                        .sorted(Comparator.comparing(Comment::getCreationTimestamp).reversed())
                        .collect(Collectors.toList()))
                .observers(getObservers())
                .producer(getProducer().toModel())
                .connectivityTechnology(connectivityTechnology)
                .turntableMaterial(turntableMaterial)
                .motorType(motorType)
                .powerSource(powerSource)
                .maximumRotationalSpeed(maximumRotationalSpeed)
                .variants(variants.stream()
                        .map(GramophonesVariantEntity::toModel)
                        .collect(Collectors.toList()))
                .build();
    }

    private boolean allAreVariantsGramophones(Set<ProductVariantEntity> variants) {
        return variants.stream()
                .allMatch(GramophonesVariantEntity.class::isInstance);
    }
}
