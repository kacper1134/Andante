package org.andante.headphones.dto;

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
public class HeadphonesVariantInputDTO extends ProductVariantInputDTO {

    @Positive(message = "Headphones variant impedance '${validatedValue}' must be a positive value")
    @NotNull(message = "Headphones variant impedance must not be null")
    private Integer nominalImpedance;

    @Positive(message = "Headphones variant loudness '${validatedValue}' must be a positive value")
    @NotNull(message = "Headphones variant loudness must not be null")
    private Integer loudness;

    @NotBlank(message = "Headphones variant color '${validatedValue}' must not be blank")
    private String color;

    @Positive(message = "Headphones identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Headphones identifier must not be null")
    private Long headphonesId;
}
