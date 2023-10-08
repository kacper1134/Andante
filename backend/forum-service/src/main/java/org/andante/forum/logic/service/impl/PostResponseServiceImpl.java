package org.andante.forum.logic.service.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import dto.response.PostResponseQuerySpecification;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.mapper.PostResponseModelEntityMapper;
import org.andante.forum.logic.model.response.PostResponseInputModel;
import org.andante.forum.logic.model.response.PostResponseOutputModel;
import org.andante.forum.logic.model.response.PostResponsesLikesRelationModel;
import org.andante.forum.logic.service.PostResponseService;
import org.andante.forum.repository.PostResponseRepository;
import org.andante.forum.repository.PostResponsesLikesRelationRepository;
import org.andante.forum.repository.entity.PostResponseEntity;
import org.andante.forum.repository.entity.PostResponsesLikesRelationEntity;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PostResponseServiceImpl implements PostResponseService {

    private final PostResponseRepository postResponseRepository;
    private final PostResponsesLikesRelationRepository postResponsesLikesRelationRepository;
    private final PostResponseModelEntityMapper postResponseMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<PostResponseEntity> rsqlVisitor;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<PostResponseOutputModel> getByQuery(PostResponseQuerySpecification postResponseQuerySpecification) {
        Node rootNode = rsqlParser.parse(postResponseQuerySpecification.getQuery());
        Specification<PostResponseEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = PageRequest.of(
                postResponseQuerySpecification.getPageNumber(),
                postResponseQuerySpecification.getPageSize(),
                Sort.by("createDate")
        );

        Page<PostResponseEntity> databaseResponse = postResponseRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(postResponseMapper::toResponseModel);

    }

    @Override
    public Long create(PostResponseInputModel postResponseInputModel) {
        PostResponseEntity postResponseEntity = postResponseMapper.toResponseEntity(postResponseInputModel);
        return postResponseRepository.save(postResponseEntity).getId();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PostResponseOutputModel likeResponse(PostResponsesLikesRelationModel postResponsesLikesRelationModel) {
        long id = postResponsesLikesRelationModel.getResponse();
        String email = postResponsesLikesRelationModel.getEmail();

        boolean isLiked = postResponsesLikesRelationRepository.isLikedByUser(id, email);

        if(isLiked) {
            PostResponsesLikesRelationEntity postResponsesLikesRelationEntity =
                    postResponsesLikesRelationRepository.findByUserAndResponse(email, id);
            postResponsesLikesRelationRepository.deleteById(postResponsesLikesRelationEntity.getId());
        } else {
            PostResponsesLikesRelationEntity postResponsesLikesRelationEntity =
                    postResponseMapper.toResponseLikeEntity(postResponsesLikesRelationModel);
            postResponsesLikesRelationRepository.save(postResponsesLikesRelationEntity);
        }

        Optional<PostResponseEntity> databaseResponse = postResponseRepository.findById(id);
        PostResponseEntity postResponseEntity = databaseResponse.get();
        return postResponseMapper.toResponseModel(postResponseEntity);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PostResponseOutputModel get(long id) {
        Optional<PostResponseEntity> databaseResponse = postResponseRepository.findById(id);
        PostResponseEntity postResponseEntity = databaseResponse.get();
        return postResponseMapper.toResponseModel(postResponseEntity);
    }

    @Override
    public OperationStatus update(PostResponseInputModel postResponseInputModel) {
        PostResponseEntity responseToUpdate = postResponseMapper.toResponseEntity(postResponseInputModel);
        postResponseRepository.save(responseToUpdate);
        return OperationStatus.OK;
    }

    @Override
    public OperationStatus delete(long id) {
        Optional<PostResponseEntity> databaseResponse = postResponseRepository.findById(id);
        databaseResponse.ifPresent(postResponseRepository::delete);
        return databaseResponse.isPresent() ? OperationStatus.OK : OperationStatus.NOT_FOUND;
    }
}
