package org.andante.speakers.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.model.ProductOutput;
import org.andante.speakers.dto.SpeakersOutputDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SpeakersOutput extends ProductOutput {

    private Boolean wireless;
    private Float bluetoothStandard;
    private List<SpeakersVariantOutput> variants;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }

    @Override
    public SpeakersOutputDTO toDTO() {
        Double averageRating = getComments().stream()
                .map(Comment::getRating)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return SpeakersOutputDTO.builder()
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
                .wireless(wireless)
                .bluetoothStandard(bluetoothStandard)
                .variants(variants.stream()
                        .map(SpeakersVariantOutput::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
