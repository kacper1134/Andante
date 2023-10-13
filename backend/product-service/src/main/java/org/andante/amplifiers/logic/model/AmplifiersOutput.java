package org.andante.amplifiers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.amplifiers.enums.AmplifierType;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.model.ProductOutput;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AmplifiersOutput extends ProductOutput {

    private Float power;
    private AmplifierType amplifierType;
    private List<AmplifiersVariantOutput> variants;

    @Override
    public AmplifiersOutputDTO toDTO() {
        Double averageRating = getComments().stream()
                .map(Comment::getRating)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return AmplifiersOutputDTO.builder()
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
                .power(power)
                .amplifierType(amplifierType)
                .variants(variants.stream()
                        .map(AmplifiersVariantOutput::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
