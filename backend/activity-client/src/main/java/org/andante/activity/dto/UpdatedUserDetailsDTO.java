package org.andante.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedUserDetailsDTO {

    @Size(min = 2, max = 100, message = "User first name '${validatedValue}' must be between {min} and {max} characters long")
    @NotBlank(message = "User first name must not be blank")
    private String firstName;

    @Size(min = 2, max = 100, message = "User last name '${validatedValue}' must be between {min} and {max} characters long")
    @NotBlank(message = "User last name must not be blank")
    private String lastName;

    @Pattern(regexp="^[0-9]{9}", message = "User phone number '${validatedValue}' must be exactly nine digits long")
    @NotBlank(message = "User phone number must not be blank")
    private String phoneNumber;

    @NotBlank(message = "User date of birth '${validatedValue}' must not be blank")
    private String dateOfBirth;

    @Size(min = 2, max = 100, message = "User country '${validatedValue}' must be between {min} and {max} characters long")
    @NotBlank(message = "User country must not be blank")
    private String country;

    @Size(min = 2, max = 150, message = "User city '${validatedValue}' must be between {min} and {max} characters long")
    @NotBlank(message = "User city must not be blank")
    private String city;

    @Size(min = 2, max = 150, message = "User street '${validatedValue}' must be between {min} and {max} characters long")
    private String street;

    @Pattern(regexp = "^[0-9]{2}-[0-9]{3}", message = "User postal code '${validatedValue}' must be match {regexp} pattern")
    private String postalCode;

    @NotBlank(message = "User gender must not be blank")
    private String gender;

    private String profileImageUrl;

    private String description;
}
