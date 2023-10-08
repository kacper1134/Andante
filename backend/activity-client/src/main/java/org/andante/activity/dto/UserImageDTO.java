package org.andante.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserImageDTO {

    @Size(min = 2, max = 150, message = "Username must be between {min} and {max} characters long")
    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Image url must not be blank")
    private String imageUrl;
}
