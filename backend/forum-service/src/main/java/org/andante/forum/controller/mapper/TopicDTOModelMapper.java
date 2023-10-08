package org.andante.forum.controller.mapper;

import dto.topic.TopicInputDTO;
import dto.topic.TopicOutputDTO;
import org.andante.forum.logic.model.topic.TopicInputModel;
import org.andante.forum.logic.model.topic.TopicOutputModel;
import org.springframework.stereotype.Component;

@Component
public class TopicDTOModelMapper {

    public TopicInputModel toModel(TopicInputDTO topicInputDTO) {
        return TopicInputModel.builder()
                .id(topicInputDTO.getId())
                .name(topicInputDTO.getName())
                .imageUrl(topicInputDTO.getImageUrl())
                .parentTopic(topicInputDTO.getParentId())
                .build();
    }

    public TopicOutputDTO toDTO(TopicOutputModel topicOutputModel) {
        return TopicOutputDTO.builder()
                .id(topicOutputModel.getId())
                .name(topicOutputModel.getName())
                .imageUrl(topicOutputModel.getImageUrl())
                .parentTopicId(topicOutputModel.getParentTopic())
                .posts(topicOutputModel.getPosts())
                .postsAmount(topicOutputModel.getPostsAmount())
                .build();
    }
}
