package org.andante.product.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.product.dto.CommentDTO;
import org.andante.product.repository.entity.CommentEntity;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class Comment {

    private Long id;
    private String username;
    private LocalDateTime creationTimestamp;
    private Float rating;
    private String title;
    private String content;
    private Long productId;
    private String productName;
    private Set<String> observers;

    public CommentDTO toDTO() {
        return CommentDTO.builder()
                .id(id)
                .creationTimestamp(creationTimestamp)
                .username(username)
                .rating(rating)
                .title(title)
                .content(content)
                .productId(productId)
                .productName(productName)
                .observers(observers)
                .build();
    }

    public CommentEntity toEntity() {
        return CommentEntity.builder()
                .id(id)
                .username(username)
                .rating(rating)
                .title(title)
                .content(content)
                .observers(observers)
                .build();
    }
}
