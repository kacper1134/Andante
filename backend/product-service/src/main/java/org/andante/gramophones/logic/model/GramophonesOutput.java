package org.andante.gramophones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.dto.GramophonesOutputDTO;
import org.andante.gramophones.enums.ConnectivityTechnology;
import org.andante.gramophones.enums.MotorType;
import org.andante.gramophones.enums.PowerSource;
import org.andante.gramophones.enums.TurntableMaterial;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.model.ProductOutput;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class GramophonesOutput extends ProductOutput {

    private ConnectivityTechnology connectivityTechnology;
    private TurntableMaterial turntableMaterial;
    private MotorType motorType;
    private PowerSource powerSource;
    private Integer maximumRotationalSpeed;
    private List<GramophonesVariantOutput> variants;

    @Override
    public GramophonesOutputDTO toDTO() {
        Double averageRating = getComments().stream()
                .map(Comment::getRating)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return GramophonesOutputDTO.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .weight(getWeight())
                .price(getBasePrice())
                .minimumFrequency(getMinimumFrequency())
                .maximumFrequency(getMaximumFrequency())
                .productType(getProductType())
                .creationTimestamp(getCreationTimestamp())
                .modificationTimestamp(getModificationTimestamp())
                .comments(getComments().stream()
                        .map(Comment::toDTO)
                        .collect(Collectors.toList()))
                .averageRating(averageRating)
                .observers(getObservers())
                .producer(getProducer().toDTO())
                .connectivityTechnology(connectivityTechnology)
                .turntableMaterial(turntableMaterial)
                .motorType(motorType)
                .powerSource(powerSource)
                .maximumRotationalSpeed(maximumRotationalSpeed)
                .variants(variants.stream()
                        .map(GramophonesVariantOutput::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
