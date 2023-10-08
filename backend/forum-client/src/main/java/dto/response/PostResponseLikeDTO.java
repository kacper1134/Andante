package dto.response;

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
public class PostResponseLikeDTO {

    @NotNull(message = "Like must be assigned to post")
    private Long id;

    @NotEmpty(message = "Like must be assigned to user")
    private String email;
}
