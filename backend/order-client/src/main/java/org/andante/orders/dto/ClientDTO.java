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
public class ClientDTO {

    @Positive(message = "The user's identifier must not be null")
    private Long id;

    @NotNull(message = "The user's email must not be null")
    @Email(message = "The user's email '${validatedValue}' is not a valid email address")
    private String emailAddress;

    @NotNull(message = "The user's name must not be null")
    @Size(min = 2, max = 100, message = "The user name '${validatedValue}' must be between {min} and {max} characters long")
    private String name;

    @Pattern(regexp="(^$|[0-9]{9})", message = "Phone number should have 9 digits")
    @NotNull(message = "The phone number must not be null")
    private String phoneNumber;

    @NotNull(message = "The user's surname must not be null")
    @Size(min = 2, max = 100, message = "The producer surname '${validatedValue}' must be between {min} and {max} characters long")
    private String surname;

    @NotNull(message = "Associated orders '${validatedValue}' must not be a null value")
    private Set<@Positive(message = "Order identifier '${validatedValue}' must be a positive number")
                @NotNull(message = "Order identifier must not be null") Long> orderIds;
}
