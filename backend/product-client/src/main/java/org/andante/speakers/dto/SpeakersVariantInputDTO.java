package org.andante.speakers.dto;

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
public class SpeakersVariantInputDTO extends ProductVariantInputDTO {

    @Positive(message = "Speakers variant loudness '${validatedValue}' must be a positive number")
    @NotNull(message = "Speakers variant loudness must not be a null")
    private Integer loudness;

    @NotBlank(message = "Speakers variant color '${validatedValue}' must not be blank")
    private String color;

    @Positive(message = "Speakers identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Speakers identifier must not be a null")
    private Long speakersId;
}
