package org.andante.forum.logic.model.response;

import lombok.Builder;
import lombok.Data;
import org.andante.forum.logic.model.UserModel;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class PostResponseOutputModel {

    private Long id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modificationDate;
    private Long post;
    private UserModel user;
    private Set<PostResponsesLikesRelationModel> likedByUsers;
    private Long likesAmount;
}
