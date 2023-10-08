package org.andante.gramophones.dto;

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
public class GramophonesVariantInputDTO extends ProductVariantInputDTO {

    @NotBlank(message = "Gramophones variant color '${validatedValue}' must not be blank")
    private String color;

    @Positive(message = "Gramophones identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Gramophones identifier must not be null")
    private Long gramophonesId;
}
