package org.andante.product.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.product.exception.ProductNotFoundException;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.ProductRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentModelEntityMapper {

    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Product with identifier %d does not exist";

    private final ProductRepository productRepository;

    public Comment toModel(CommentEntity commentEntity) {
        return commentEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CommentEntity toEntity(Comment comment) {
        ProductEntity productEntity = productRepository.findById(comment.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(String.format(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE, comment.getProductId())));

        return CommentEntity.builder()
                .id(comment.getId())
                .username(comment.getUsername())
                .rating(comment.getRating())
                .title(comment.getTitle())
                .content(comment.getContent())
                .product(productEntity)
                .observers(Optional.ofNullable(comment.getObservers()).orElse(Set.of()))
                .build();
    }
}
