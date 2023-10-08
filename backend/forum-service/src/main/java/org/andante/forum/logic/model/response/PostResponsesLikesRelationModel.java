package org.andante.forum.logic.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostResponsesLikesRelationModel {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private String email;
    private Long response;
}
