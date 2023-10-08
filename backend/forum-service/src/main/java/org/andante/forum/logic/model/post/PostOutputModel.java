package org.andante.forum.logic.model.post;

import lombok.Builder;
import lombok.Data;
import org.andante.forum.logic.model.UserModel;
import org.andante.forum.logic.model.topic.TopicOutputModel;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class PostOutputModel {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime creationTimestamp;
    private LocalDateTime modificationTimestamp;
    private TopicOutputModel topic;
    private UserModel user;
    private Set<Long> responses;
    private Set<PostLikesRelationModel> userLikes;
    private int likesAmount;
    private int responsesAmount;
}
