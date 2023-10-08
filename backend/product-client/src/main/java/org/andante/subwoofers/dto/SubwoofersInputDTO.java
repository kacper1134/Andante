package org.andante.subwoofers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductInputDTO;
import org.andante.subwoofers.enums.SubwooferType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubwoofersInputDTO extends ProductInputDTO {

    @Positive(message = "Subwoofers power '${validatedValue}' must be a positive number")
    @NotNull(message = "Subwoofers power must not be a null")
    private Float power;

    @NotNull(message = "Subwoofers type must be one of allowed values")
    private SubwooferType type;

    @NotNull(message = "Provided list of variants must not be a null")
    private Set<@Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Variant identifier must not be a null") Long> variantsIds;
}
