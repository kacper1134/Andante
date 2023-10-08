package org.andante.forum.controller.mapper;

import dto.response.PostResponseLikeDTO;
import org.andante.forum.logic.model.response.PostResponsesLikesRelationModel;
import org.springframework.stereotype.Component;

@Component
public class PostResponsesLikesDTOModelMapper {

    public PostResponsesLikesRelationModel toModel(PostResponseLikeDTO postResponseLikeDTO) {
        return PostResponsesLikesRelationModel.builder()
                .response(postResponseLikeDTO.getId())
                .email(postResponseLikeDTO.getEmail())
                .build();
    }

    public PostResponseLikeDTO toDTO(PostResponsesLikesRelationModel postResponsesLikesRelationModel) {
        return PostResponseLikeDTO.builder()
                .id(postResponsesLikesRelationModel.getResponse())
                .email(postResponsesLikesRelationModel.getEmail())
                .build();
    }
}
