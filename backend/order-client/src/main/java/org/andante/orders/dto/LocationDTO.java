package org.andante.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {

    @Positive(message = "Location identifier '${validatedValue}' must be a positive number")
    private Long id;

    @NotNull(message = "City must not be a null")
    @Size(min = 2, max = 100, message = "City '${validatedValue}' must be between {min} and {max} characters long")
    private String city;

    @NotNull(message = "Country must not be null")
    @Size(min = 2, max = 100, message = "Country '${validatedValue}' must be between {min} and {max} characters long")
    private String country;

    @Min(value = 1, message = "Flat number '${validatedValue}' must not be lesser than {value}")
    private Long flatNumber;

    @Pattern(regexp="(^[0-9]{2}-[0-9]{3})", message = "Post code must have 2 digits before and 3 digits after dash")
    @NotNull(message = "Post code must not be null")
    private String postCode;


    @Size(min = 2, max = 100, message = "Street name '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Street must not be null")
    private String street;

    @NotNull(message = "Street number must not be null")
    private String streetNumber;


    @NotNull(message = "Associated orders '${validatedValue}' must not be a null value")
    private Set<@Positive(message = "Order identifier must be a positive number")
                @NotNull(message = "Order identifier must not be null") Long> orderIds;

    @NotNull(message = "Associated delivery orders '${validatedValue}' must not be a null value")
    private Set<@Positive(message = "Order identifier must be a positive number")
                @NotNull(message = "Order identifier must note be a null") Long> deliveryOrdersIds;
}
