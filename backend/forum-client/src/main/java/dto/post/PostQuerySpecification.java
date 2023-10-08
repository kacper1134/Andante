package dto.post;

import enums.PostSortingOrder;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class PostQuerySpecification {

    @NotBlank(message = "Query '${validatedValue}' must not be a blank value")
    private String query;

    @PositiveOrZero(message = "Page number '${validatedValue}' must not be a negative number")
    @NotNull(message = "Page number must not be a null")
    private Integer pageNumber;

    @Positive(message = "Page size '${validatedValue}' must be a positive number")
    @NotNull(message = "Page size must not be a null")
    private Integer pageSize;

    private PostSortingOrder sortingOrder;
}
