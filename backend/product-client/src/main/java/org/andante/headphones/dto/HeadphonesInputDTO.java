package org.andante.headphones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.headphones.constraint.HeadphonesInputConstraint;
import org.andante.headphones.enums.ConstructionType;
import org.andante.headphones.enums.DriverType;
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
@HeadphonesInputConstraint
public class HeadphonesInputDTO extends ProductInputDTO {

    @NotNull(message = "Headphones construction type must be one of allowed values")
    private ConstructionType constructionType;

    @NotNull(message = "Headphones driver type must be one of allowed values")
    private DriverType driverType;

    @NotNull(message = "Headphones wireless status '${validatedValue}' must not be a null")
    private Boolean wireless;

    private Float bluetoothStandard;

    @NotNull(message = "Provided list of variants must not be a null")
    private Set<@Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Variant identifier must not be null") Long> variantsIds;

    public Optional<Float> getBluetoothStandard() {
        return Optional.ofNullable(bluetoothStandard);
    }
}
