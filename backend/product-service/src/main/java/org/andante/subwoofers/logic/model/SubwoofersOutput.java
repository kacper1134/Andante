package org.andante.subwoofers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.model.ProductOutput;
import org.andante.subwoofers.dto.SubwoofersOutputDTO;
import org.andante.subwoofers.enums.SubwooferType;

import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SubwoofersOutput extends ProductOutput {

    private Float power;
    private SubwooferType type;
    private List<SubwoofersVariantOutput> variants;

    @Override
    public SubwoofersOutputDTO toDTO() {
        Double averageRating = getComments().stream()
                .map(Comment::getRating)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return SubwoofersOutputDTO.builder()
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
                .type(type)
                .variants(variants.stream()
                        .map(SubwoofersVariantOutput::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
