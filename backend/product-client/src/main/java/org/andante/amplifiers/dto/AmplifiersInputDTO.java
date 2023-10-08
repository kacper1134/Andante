package org.andante.amplifiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.enums.AmplifierType;
import org.andante.product.dto.ProductInputDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AmplifiersInputDTO extends ProductInputDTO {

    @Positive(message = "Amplifiers power '${validatedValue}' must be a positive number")
    @NotNull(message = "Amplifiers power must not be null")
    private Float power;

    @NotNull(message = "Amplifier type must be one of allowed values")
    private AmplifierType amplifierType;

    @NotNull(message = "List of provided variants '${validatedValue}' must not be a null")
    private Set<@Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Variant identifier must not be null") Long> variantsIds;
}
