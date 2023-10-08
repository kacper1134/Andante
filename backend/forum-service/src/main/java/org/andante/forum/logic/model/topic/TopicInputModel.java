package org.andante.forum.logic.model.topic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicInputModel {

    private Long id;
    private String name;
    private String imageUrl;
    private Long parentTopic;

}
