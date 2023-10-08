package org.andante.gramophones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.gramophones.enums.ConnectivityTechnology;
import org.andante.gramophones.enums.MotorType;
import org.andante.gramophones.enums.PowerSource;
import org.andante.gramophones.enums.TurntableMaterial;
import org.andante.product.dto.ProductInputDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GramophonesInputDTO extends ProductInputDTO {

    @NotNull(message = "Gramophone connectivity technology must be one of allowed values")
    private ConnectivityTechnology connectivityTechnology;

    @NotNull(message = "Gramophone turntable material must be one of allowed values")
    private TurntableMaterial turntableMaterial;

    @NotNull(message = "Gramophone motor type must be one of allowed values")
    private MotorType motorType;

    @NotNull(message = "Gramophone power source must be one of allowed values")
    private PowerSource powerSource;

    @Positive(message = "Gramophone rotational speed '${validatedValue}' must be a positive value")
    @NotNull(message = "Gramophone rotational speed must not be null")
    private Integer maximumRotationalSpeed;

    @NotNull(message = "Provided list of variants must not be a null")
    private Set<@Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Variant identifier must not be null") Long> variantsIds;
}
