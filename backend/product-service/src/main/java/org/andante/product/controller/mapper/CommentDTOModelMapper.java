package org.andante.product.controller.mapper;

import org.andante.product.dto.CommentDTO;
import org.andante.product.logic.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentDTOModelMapper {

    public CommentDTO toDTO(Comment comment) {
        return comment.toDTO();
    }

    public Comment toModel(CommentDTO commentDTO) {
        return Comment.builder()
                .id(commentDTO.getId())
                .title(commentDTO.getTitle())
                .rating(commentDTO.getRating())
                .username(commentDTO.getUsername())
                .productId(commentDTO.getProductId())
                .content(commentDTO.getContent())
                .observers(commentDTO.getObservers())
                .build();
    }
}
