package dto.response;

import dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseOutputDTO {

    private Long id;
    private String content;
    private LocalDateTime creationTimestamp;
    private LocalDateTime modificationTimestamp;
    private Long post;
    private UserDTO user;
    private Set<PostResponseLikeDTO> likes;
    private Long likesAmount;
}
