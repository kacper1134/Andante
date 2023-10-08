package org.andante.forum.controller.mapper;

import dto.post.PostLikeDTO;
import org.andante.forum.logic.model.post.PostLikesRelationModel;
import org.springframework.stereotype.Component;

@Component
public class PostLikesDTOModelMapper {

    public PostLikesRelationModel toModel(PostLikeDTO postLikeDTO) {
        return PostLikesRelationModel.builder()
                .post(postLikeDTO.getId())
                .user(postLikeDTO.getEmail())
                .build();
    }

    public PostLikeDTO toDTO(PostLikesRelationModel postLikesRelationModel) {
        return PostLikeDTO.builder()
                .id(postLikesRelationModel.getPost())
                .email(postLikesRelationModel.getUser())
                .build();
    }
}
