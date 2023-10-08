package dto.post;

import dto.UserDTO;
import dto.topic.TopicOutputDTO;
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
public class PostOutputDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime creationTimestamp;
    private LocalDateTime modificationTimestamp;
    private TopicOutputDTO topic;
    private UserDTO user;
    private Boolean isLiked;
    private Set<PostLikeDTO> likes;
    private Integer likesAmount;
    private Integer responsesAmount;
}
