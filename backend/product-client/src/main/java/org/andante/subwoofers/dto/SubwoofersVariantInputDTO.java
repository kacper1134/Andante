package org.andante.subwoofers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductVariantInputDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubwoofersVariantInputDTO extends ProductVariantInputDTO {

    @NotBlank(message = "Subwoofers variant color '${validatedValue}' must not be blank")
    private String color;

    @Positive(message = "Subwoofers identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Subwoofers identifier must not be a null")
    private Long subwoofersId;
}
