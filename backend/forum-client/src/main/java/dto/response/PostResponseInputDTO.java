package dto.response;

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
public class PostResponseInputDTO {

    private Long id;

    @NotEmpty(message = "Response content must not be empty")
    @Size(min = 0, max = 5000, message = "Response content must be between {min} and {max} characters long")
    private String content;

    @NotNull(message = "Response must be assigned to post")
    private Long postId;

    @NotEmpty(message = "Response must be assigned to user")
    private String email;
}
