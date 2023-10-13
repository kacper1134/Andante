package org.andante.microphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.dto.MicrophonesOutputDTO;
import org.andante.microphones.enums.MicrophoneType;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.model.ProductOutput;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class MicrophonesOutput extends ProductOutput {

    private Boolean wireless;
    private Float bluetoothStandard;
    private MicrophoneType type;
    private List<MicrophonesVariantOutput> variants;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }

    @Override
    public MicrophonesOutputDTO toDTO() {
        Double averageRating = getComments().stream()
                .map(Comment::getRating)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return MicrophonesOutputDTO.builder()
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
                .type(type)
                .variants(variants.stream()
                        .map(MicrophonesVariantOutput::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
