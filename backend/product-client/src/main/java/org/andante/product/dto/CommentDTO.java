package org.andante.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    @Positive(message = "Comment identifier '${validatedValue}' must be positive")
    private Long id;

    @NotBlank(message = "The author's username must not be blank")
    private String username;

    @PastOrPresent(message = "Comment could not have been created in the future")
    private LocalDateTime creationTimestamp;

    @Min(value = 0, message = "Rating '${validatedValue}' is lesser than minimum allowed value of {value}")
    @Max(value = 5, message = "Rating `${validatedValue}` is greater than maximum allowed value of {value}")
    @NotNull(message = "Rating must not be null")
    private Float rating;

    @Size(min = 3, max = 100, message = "The comment title '${validatedValue}' must be between {min} and {max} characters long")
    @NotNull(message = "Comment title must not be a null")
    private String title;

    @Size(min = 0, max = 5000, message = "Comment '${validatedValue}' must not be longer than {max} characters!")
    @NotNull(message = "Comment content must not be a null")
    private String content;

    @Positive(message = "Product identifier '${validatedValue}' must be a positive number")
    @NotNull(message = "Product identifier '${validatedValue}' must not be null")
    private Long productId;

    private String productName;

    @NotNull(message = "Observers must not be a null value")
    private Set<String> observers;
}
