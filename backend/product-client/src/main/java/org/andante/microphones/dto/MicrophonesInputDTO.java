package org.andante.microphones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.microphones.constraint.MicrophonesInputConstraint;
import org.andante.microphones.enums.MicrophoneType;
import org.andante.product.dto.ProductInputDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@MicrophonesInputConstraint
public class MicrophonesInputDTO extends ProductInputDTO {

    @NotNull(message = "Microphones wireless status '${validatedValue}' must not be a null")
    private Boolean wireless;
    private Float bluetoothStandard;

    @NotNull(message = "Microphones type must be one of allowed values")
    private MicrophoneType type;

    @NotNull(message = "Provided list of variants must not be a null")
    private Set<@Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Variant identifier must not be null") Long> variantsIds;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }
}
