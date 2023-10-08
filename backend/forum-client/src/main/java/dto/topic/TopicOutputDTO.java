package dto.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicOutputDTO {

    private Long id;
    private String name;
    private String imageUrl;
    private Long parentTopicId;
    private Set<Long> posts;
    private Long postsAmount;
}