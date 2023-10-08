package org.andante.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProducerDTO {

    @Size(min = 2, max = 100, message = "The producer name '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Producer name must not be a null")
    private String name;

    @URL(regexp = "^(http|https).*", message = "Producer's website address '${validatedValue}' must be a valid url")
    @NotNull(message = "Producer's website address must not be a null")
    private String websiteUrl;

    @NotBlank(message = "Producer image address must not be blank")
    @Size(max = 200, message = "Producer image address must not be longer than {max} characters")
    private String imageUrl;

    @NotNull(message = "Associated products '${validatedValue}' must not be a null value")
    private Set<@Positive(message = "Product identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Product identifier '${validatedValue}' must not be a null") Long> productsIds;
}
