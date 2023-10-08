package org.andante.forum.logic.model.topic;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class TopicOutputModel {

    private Long id;
    private String name;
    private String imageUrl;
    private LocalDateTime creationTimestamp;
    private LocalDateTime modificationTimestamp;
    private Set<TopicOutputModel> childTopics;
    private Long parentTopic;
    private Set<Long> posts;
    private Long postsAmount;
}
