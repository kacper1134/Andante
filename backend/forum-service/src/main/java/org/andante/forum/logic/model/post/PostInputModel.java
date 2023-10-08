package org.andante.forum.logic.model.post;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostInputModel {

    private Long id;
    private String title;
    private String content;
    private Long topic;
    private String user;
}
