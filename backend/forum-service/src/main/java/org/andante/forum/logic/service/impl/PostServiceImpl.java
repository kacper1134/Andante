package org.andante.forum.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import dto.post.PostQuerySpecification;
import dto.post.TopQuerySpecification;
import enums.PostSortingOrder;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.mapper.PostModelEntityMapper;
import org.andante.forum.logic.model.post.PostInputModel;
import org.andante.forum.logic.model.post.PostLikesRelationModel;
import org.andante.forum.logic.model.post.PostOutputModel;
import org.andante.forum.logic.service.PostService;
import org.andante.forum.repository.PostLikesRelationRepository;
import org.andante.forum.repository.PostRepository;
import org.andante.forum.repository.entity.PostEntity;
import org.andante.forum.repository.entity.PostLikesRelationEntity;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikesRelationRepository postLikesRelationRepository;
    private final PostModelEntityMapper postMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<PostEntity> rsqlVisitor;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long create(PostInputModel postModel) {
        PostEntity postToCreate = postMapper.toPostEntity(postModel);
        return postRepository.save(postToCreate).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<PostOutputModel> getByQuery(PostQuerySpecification postQuerySpecification) {
        Node rootNode = rsqlParser.parse(postQuerySpecification.getQuery());
        Specification<PostEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = PageRequest.of(
                postQuerySpecification.getPageNumber(),
                postQuerySpecification.getPageSize(),
                getSortingMethod(postQuerySpecification.getSortingOrder())
        );

        Page<PostEntity> databaseResponse = postRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(postMapper::toPostOutputModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<PostOutputModel> getLikedByUser(String emailAddress) {
        Set<PostEntity> likedByUser = postLikesRelationRepository.findAllPostsByEmailAddress(emailAddress);

        return likedByUser.stream()
                .map(postMapper::toPostOutputModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<PostOutputModel> getTopPage(TopQuerySpecification topQuerySpecification) {
        Pageable pageSpecification = PageRequest.of(
                topQuerySpecification.getPageNumber(),
                topQuerySpecification.getPageSize()
        );

        Page<PostEntity> databaseResponse =
                postRepository.getTopicTopPosts(topQuerySpecification.getTopicId(), pageSpecification);

        return databaseResponse.stream()
                .map(postMapper::toPostOutputModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PostOutputModel likePost(PostLikesRelationModel postLikesRelationModel) {
        long id = postLikesRelationModel.getPost();
        String email = postLikesRelationModel.getUser();

        Optional<PostLikesRelationEntity> postLikesDatabaseResponse =
                postLikesRelationRepository.getByPostAndEmail(id, email);

        if(postLikesDatabaseResponse.isPresent()) {
            postLikesRelationRepository.delete(postLikesDatabaseResponse.get());
        } else {
            PostLikesRelationEntity postLikesRelationEntity = postMapper.toPostLikesEntity(postLikesRelationModel);
            postLikesRelationRepository.save(postLikesRelationEntity);
        }

        Optional<PostEntity> databaseResponse = postRepository.findById(id);
        PostEntity postEntity = databaseResponse.get();
        return postMapper.toPostOutputModel(postEntity);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PostOutputModel getPost(long id) {
        Optional<PostEntity> databaseResponse = postRepository.findById(id);
        PostEntity postEntity = databaseResponse.get();
        return postMapper.toPostOutputModel(postEntity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus delete(long id) {
        Optional<PostEntity> databaseResponse = postRepository.findById(id);
        databaseResponse.ifPresent(postRepository::delete);
        return databaseResponse.isPresent() ? OperationStatus.OK : OperationStatus.NOT_FOUND;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus update(PostInputModel postModel) {
        PostEntity postToUpdate = postMapper.toPostEntity(postModel);
        postRepository.save(postToUpdate);
        return OperationStatus.OK;
    }

    private Sort getSortingMethod(PostSortingOrder postSortingOrder) {
        switch (postSortingOrder) {
            case NEWEST_FIRST:
                return Sort.by("creationTimestamp").descending();
            case OLDEST_FIRST:
                return Sort.by("creationTimestamp");
            case ALPHABETICAL:
                return Sort.by("title");
            default:
                return Sort.by("title").descending();
        }
    }
}
