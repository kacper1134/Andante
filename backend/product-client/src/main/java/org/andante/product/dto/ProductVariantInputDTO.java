package org.andante.product.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public class ProductVariantInputDTO {

    @Positive(message = "Variant identifier '${validatedValue}' must be a positive number")
    private Long id;

    @NotNull(message = "Variant price difference must not be a null")
    private BigDecimal priceDifference;

    @PositiveOrZero(message = "Variant available quantity must not be a negative value")
    @NotNull(message = "Variant available quantity must not be a null")
    private Integer availableQuantity;

    @NotBlank(message = "Variant's image address must not be blank")
    @Size(max = 200, message = "Variant's image address must not be longer than {max} characters")
    private String imageUrl;

    @NotBlank(message = "Variant thumbnail image address must not be blank")
    @Size(max = 200, message = "Variant's thumbnail image address must not be longer than {max} characters")
    private String thumbnailUrl;
}
