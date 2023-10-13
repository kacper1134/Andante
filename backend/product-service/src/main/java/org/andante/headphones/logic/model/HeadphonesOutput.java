package org.andante.headphones.logic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.dto.HeadphonesOutputDTO;
import org.andante.headphones.enums.ConstructionType;
import org.andante.headphones.enums.DriverType;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.model.ProductOutput;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class HeadphonesOutput extends ProductOutput {

    private ConstructionType constructionType;
    private DriverType driverType;
    private Boolean wireless;
    private Float bluetoothStandard;
    private List<HeadphonesVariantOutput> variants;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }

    @Override
    public HeadphonesOutputDTO toDTO() {
        Double averageRating = getComments().stream()
                .map(Comment::getRating)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return HeadphonesOutputDTO.builder()
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
                .constructionType(constructionType)
                .driverType(driverType)
                .wireless(wireless)
                .bluetoothStandard(bluetoothStandard)
                .variants(variants.stream()
                        .map(HeadphonesVariantOutput::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
