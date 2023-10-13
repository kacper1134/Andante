package org.andante.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.constraint.ProductInputConstraint;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ProductInputConstraint
public abstract class ProductInputDTO {

    @Positive(message = "Product identifier '${validatedValue}' must be a positive number")
    private Long id;

    @Size(min = 1, max = 200, message = "Product name '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Product name must not be a null")
    private String name;

    @Size(min = 1, max = 5000, message = "Product description '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Product description must not be null")
    private String description;

    @Min(value = 0, message = "Product weight '${validatedValue}' must not be lesser than {value} g")
    @NotNull(message = "Product weight must not be a null")
    private Float weight;

    @DecimalMin(value = "0", message = "Product price '${validatedValue}' must not be lesser than ${value}")
    @NotNull(message = "Product price must not be a null")
    private BigDecimal price;

    @Positive(message = "Product minimum frequency '${validatedValue}' must be a positive number")
    @NotNull(message = "Product minimum frequency must not be a null")
    private Long minimumFrequency;

    @Positive(message = "Product maximum frequency '${validatedValue}' must be a positive number")
    @NotNull(message = "Product maximum frequency must not be a null")
    private Long maximumFrequency;

    @NotNull(message = "Product's related comments '${validatedValue}' must not be a null value")
    private Set<@Positive(message = "Comment id '${validatedValue}' must be a positive number")
                @NotNull(message = "Comment identifier must not be a null") Long> commentIds;

    @NotNull(message = "Product's observers '${validatedValue}' must not be a null value")
    private Set<@Email(message = "Observer's email address '${validatedValue}' is not valid")
                @NotNull(message = "Observer's email address must not be a null") String> observers;

    @Size(min = 2, max = 100, message = "The producer name '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Producer name must not be a null")
    private String producerName;
}
