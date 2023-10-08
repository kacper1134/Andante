package dto.topic;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class TopicQuerySpecification {

    @NotBlank(message = "Query '${validatedValue}' must not be a blank value")
    private String query;

    @PositiveOrZero(message = "Page number '${validatedValue}' must not be a negative number")
    @NotNull(message = "Page number must not be a null")
    private Integer pageNumber;

    @Positive(message = "Page amount '${validatedValue}' must be a positive number")
    @NotNull(message = "Page amount must not be a null")
    private Integer pageAmount;

    @NotNull(message = "Topic sorting order must be one of allowed values")
    private TopicSortingOrder sortingOrder;
}
