package org.andante.speakers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductInputDTO;
import org.andante.speakers.constraint.SpeakersInputConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SpeakersInputConstraint
public class SpeakersInputDTO extends ProductInputDTO {

    @NotNull(message = "Speakers wireless status '${validatedValue}' must not be a null")
    private Boolean wireless;
    private Float bluetoothStandard;

    @NotNull(message = "Provided list of variants must not be a null")
    private Set<@Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Variant identifier must not be a null") Long> variantsIds;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }
}
