package dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostInputDTO {

    private Long id;

    @NotEmpty(message = "Post title must not be empty")
    @Size(min = 0, max = 150, message = "Post title must be between {min} and {max} characters long")
    private String title;

    @NotEmpty(message = "Post content must not be empty")
    @Size(min = 0, max = 5000, message = "Post content must be between {min} and {max} characters long")
    private String content;

    @NotNull(message = "Post must be assigned to topic")
    private Long topicId;

    @NotEmpty(message = "Post must be assigned to user")
    private String email;
}
