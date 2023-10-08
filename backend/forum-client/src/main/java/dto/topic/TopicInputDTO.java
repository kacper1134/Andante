package dto.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicInputDTO {

    private Long id;

    @NotEmpty(message = "Topic name must not be empty")
    private String name;

    @NotNull(message = "Topic title must not be null")
    private String imageUrl;

    private Long parentId;
}