package org.andante.product.logic.service;

import org.andante.enums.OperationStatus;
import org.andante.product.dto.CommentQuerySpecification;
import org.andante.product.dto.CommentStatistics;
import org.andante.product.logic.model.Comment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface CommentService {

    Set<Comment> getComments(List<Long> identifiers);
    Set<Comment> getProductComments(Long productId);
    List<Comment> getTopComments(String username, Integer page, Integer count);
    Set<Comment> getAllObserved(String email);
    Page<Comment> getByQuery(CommentQuerySpecification commentQuerySpecification);
    CommentStatistics getStatistics(String username);
    OperationStatus changeObservationStatus(String observer, Long id);
    Comment createComment(Comment comment);
    Comment updateComment(Comment comment);
    Comment deleteComment(Long id);
}
