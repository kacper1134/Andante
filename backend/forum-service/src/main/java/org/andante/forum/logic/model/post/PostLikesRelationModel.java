package org.andante.forum.logic.model.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostLikesRelationModel {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private String user;
    private Long post;
}
