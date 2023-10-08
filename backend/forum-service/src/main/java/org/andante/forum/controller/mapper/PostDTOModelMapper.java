package org.andante.forum.controller.mapper;

import dto.post.PostInputDTO;
import dto.post.PostOutputDTO;
import lombok.RequiredArgsConstructor;
import org.andante.forum.logic.model.post.PostInputModel;
import org.andante.forum.logic.model.post.PostOutputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PostDTOModelMapper {

    private final TopicDTOModelMapper topicDTOModelMapper;
    private final UserDTOModelMapper userDTOModelMapper;
    private final PostLikesDTOModelMapper postLikesMapper;

    public PostInputModel toModel(PostInputDTO postInputDTO) {
        return PostInputModel.builder()
                .id(postInputDTO.getId())
                .title(postInputDTO.getTitle())
                .content(postInputDTO.getContent())
                .topic(postInputDTO.getTopicId())
                .user(postInputDTO.getEmail())
                .build();
    }

    public PostOutputDTO toDTO(PostOutputModel postOutputModel) {
        return PostOutputDTO.builder()
                .id(postOutputModel.getId())
                .title(postOutputModel.getTitle())
                .content(postOutputModel.getContent())
                .modificationTimestamp(postOutputModel.getModificationTimestamp())
                .creationTimestamp(postOutputModel.getCreationTimestamp())
                .topic(topicDTOModelMapper.toDTO(postOutputModel.getTopic()))
                .user(userDTOModelMapper.toDTO(postOutputModel.getUser()))
                .likes(postOutputModel.getUserLikes().stream()
                        .map(postLikesMapper::toDTO)
                        .collect(Collectors.toSet()))
                .likesAmount(postOutputModel.getLikesAmount())
                .responsesAmount(postOutputModel.getResponsesAmount())
                .build();
    }
}
