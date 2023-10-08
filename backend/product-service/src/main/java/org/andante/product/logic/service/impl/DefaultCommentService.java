package org.andante.product.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.product.dto.CommentQuerySpecification;
import org.andante.product.dto.CommentStatistics;
import org.andante.product.enums.CommentSortingOrder;
import org.andante.product.exception.CommentConflictException;
import org.andante.product.exception.CommentNotFoundException;
import org.andante.product.logic.mapper.CommentModelEntityMapper;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.service.CommentService;
import org.andante.product.repository.CommentRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.rsql.PersistentRSQLVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultCommentService implements CommentService {

    private static final String COMMENT_CONFLICT_EXCEPTION_MESSAGE = "Comment with identifier %d already exists";
    private static final String COMMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Comment with identifier %d does not exist";

    private final CommentRepository commentRepository;
    private final CommentModelEntityMapper commentModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<CommentEntity> rsqlVisitor;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<Comment> getComments(List<Long> identifiers) {
        List<CommentEntity> databaseResponse = commentRepository.findAllById(identifiers);

        return databaseResponse.stream()
                .map(commentModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<Comment> getProductComments(Long productId) {
        Set<CommentEntity> databaseResponse = commentRepository.findAllByProductId(productId);

        return databaseResponse.stream()
                .map(commentModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<Comment> getAllObserved(String email) {
        Set<CommentEntity> databaseResponse = commentRepository.findAllByObserver(email);

        return databaseResponse.stream()
                .map(commentModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED , readOnly = true)
    public Page<Comment> getByQuery(CommentQuerySpecification commentQuerySpecification) {
        Node rootNode = rsqlParser.parse(commentQuerySpecification.getQuery());
        Specification<CommentEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = getPageSpecification(commentQuerySpecification);

        Page<CommentEntity> databaseResponse = commentRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(commentModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Comment> getTopComments(String username, Integer page, Integer count) {
        PageRequest pageRequest = PageRequest.of(page, count);

        List<CommentEntity> databaseResponse = commentRepository.findTopPosts(username, pageRequest);

        return databaseResponse.stream()
                .map(commentModelEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CommentStatistics getStatistics(String username) {

        Integer commentsCount = commentRepository.countByUsername(username);
        Integer upvoteCount = commentRepository.countLikes(username);

        return CommentStatistics.builder()
                .username(username)
                .commentsCount(commentsCount)
                .upvoteCount(upvoteCount)
                .build();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus changeObservationStatus(String observer, Long id) {
        Optional<CommentEntity> databaseResponse = commentRepository.findById(id);

        if (databaseResponse.isEmpty()) {
            return OperationStatus.NOT_FOUND;
        }

        CommentEntity observedComment = databaseResponse.get();

        if (observedComment.getObservers().contains(observer)) {
            observedComment.getObservers().remove(observer);
        } else {
            observedComment.getObservers().add(observer);
        }

        commentRepository.save(observedComment);

        return OperationStatus.OK;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Comment createComment(Comment comment) {
        if (comment.getId() != null && commentRepository.existsById(comment.getId())) {
            throw new CommentConflictException(String.format(COMMENT_CONFLICT_EXCEPTION_MESSAGE, comment.getId()));
        }

        CommentEntity commentToCreate = commentModelEntityMapper.toEntity(comment);

        CommentEntity createdComment = commentRepository.save(commentToCreate);

        return commentModelEntityMapper.toModel(createdComment);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Comment updateComment(Comment comment) {
        if (comment.getId() == null || !commentRepository.existsById(comment.getId())) {
            throw new CommentNotFoundException(String.format(COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, comment.getId()));
        }

        CommentEntity commentToUpdate = commentModelEntityMapper.toEntity(comment);

        CommentEntity updatedComment = commentRepository.save(commentToUpdate);

        return commentModelEntityMapper.toModel(updatedComment);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Comment deleteComment(Long id) {
        Optional<CommentEntity> databaseResponse = commentRepository.findById(id);

        if (databaseResponse.isEmpty()) {
            throw new CommentNotFoundException(String.format(COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        commentRepository.delete(databaseResponse.get());

        return commentModelEntityMapper.toModel(databaseResponse.get());
    }

    private Pageable getPageSpecification(CommentQuerySpecification commentQuerySpecification) {
        return PageRequest.of(commentQuerySpecification.getPage(), commentQuerySpecification.getPageSize(),
                getSortingMethod(commentQuerySpecification.getSortingOrder()));
    }

    private Sort getSortingMethod(CommentSortingOrder commentSortingOrder) {
        switch (commentSortingOrder) {
            case NEWEST_FIRST:
                return Sort.by("creationTimestamp").descending();
            case OLDEST_FIRST:
                return Sort.by("creationTimestamp");
            case HIGHEST_RATING:
                return Sort.by("rating").descending();
            default:
                return Sort.by("rating");
        }
    }
}
