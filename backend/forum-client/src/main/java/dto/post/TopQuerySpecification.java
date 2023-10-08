package dto.post;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class TopQuerySpecification {

    @PositiveOrZero(message = "Topic id '${validatedValue}' must not be a negative number")
    @NotNull(message = "Topic id must not be a null")
    private Long topicId;

    @PositiveOrZero(message = "Page number '${validatedValue}' must not be a negative number")
    @NotNull(message = "Page number must not be a null")
    private Integer pageNumber;

    @Positive(message = "Page size '${validatedValue}' must be a positive number")
    @NotNull(message = "Page size must not be a null")
    private Integer pageSize;
}
