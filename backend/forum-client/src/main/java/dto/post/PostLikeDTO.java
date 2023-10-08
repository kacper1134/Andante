package dto.post;

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
public class PostLikeDTO {

    @NotNull(message = "Response must be assigned to post")
    private Long id;

    @NotEmpty(message = "Response must be assigned to user")
    private String email;
}
