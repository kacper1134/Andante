package org.andante.forum.controller.mapper;

import dto.response.PostResponseInputDTO;
import dto.response.PostResponseOutputDTO;
import lombok.RequiredArgsConstructor;
import org.andante.forum.logic.model.response.PostResponseInputModel;
import org.andante.forum.logic.model.response.PostResponseOutputModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PostResponseDTOModelMapper {

    private final UserDTOModelMapper userMapper;
    private final PostResponsesLikesDTOModelMapper postResponsesLikesMapper;

    public PostResponseInputModel toModel(PostResponseInputDTO postResponseInputDTO) {
        return PostResponseInputModel.builder()
                .id(postResponseInputDTO.getId())
                .content(postResponseInputDTO.getContent())
                .post(postResponseInputDTO.getPostId())
                .user(postResponseInputDTO.getEmail())
                .build();
    }

    public PostResponseOutputDTO toDTO(PostResponseOutputModel postResponseOutputModel) {
        return PostResponseOutputDTO.builder()
                .id(postResponseOutputModel.getId())
                .content(postResponseOutputModel.getContent())
                .creationTimestamp(postResponseOutputModel.getCreateDate())
                .modificationTimestamp(postResponseOutputModel.getModificationDate())
                .user(userMapper.toDTO(postResponseOutputModel.getUser()))
                .post(postResponseOutputModel.getPost())
                .likes(postResponseOutputModel.getLikedByUsers().stream()
                        .map(postResponsesLikesMapper::toDTO)
                        .collect(Collectors.toSet()))
                .likesAmount(postResponseOutputModel.getLikesAmount())
                .build();
    }
}
