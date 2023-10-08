package org.andante.forum.logic.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostResponseInputModel {

    private Long id;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime modificationDate;
    private Long post;
    private String user;
}
