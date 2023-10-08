package org.andante.forum.logic.service.impl;

import com.google.common.collect.Lists;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import dto.topic.TopicQuerySpecification;
import dto.topic.TopicSortingOrder;
import exception.TopicNotFoundException;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.mapper.TopicModelEntityMapper;
import org.andante.forum.logic.model.topic.TopicInputModel;
import org.andante.forum.logic.model.topic.TopicOutputModel;
import org.andante.forum.logic.service.TopicService;
import org.andante.forum.repository.TopicRepository;
import org.andante.forum.repository.entity.TopicEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TopicServiceImpl implements TopicService {

    private static final String TOPIC_NOT_FOUND_EXCEPTION_MESSAGE = "Topic with identifier %d does not exist";

    private final TopicRepository topicRepository;
    private final TopicModelEntityMapper topicMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<TopicEntity> rsqlVisitor;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long create(TopicInputModel topicOutputModel) {
        TopicEntity topicEntity = topicMapper.toEntity(topicOutputModel);
        return topicRepository.save(topicEntity).getId();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<TopicOutputModel> getByQuery(TopicQuerySpecification topicQuerySpecification) {
        Node rootNode = rsqlParser.parse(topicQuerySpecification.getQuery());
        Specification<TopicEntity> specification = rootNode.accept(rsqlVisitor);

        Pageable pageSpecification = PageRequest.of(
                topicQuerySpecification.getPageNumber(),
                topicQuerySpecification.getPageAmount(),
                getSortingOrder(topicQuerySpecification.getSortingOrder())
        );

        Page<TopicEntity> databaseResponse = topicRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(topicMapper::toModel);

    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<TopicOutputModel> getTop(Integer page, Integer count) {
        Pageable pageable = PageRequest.of(page, count);

        Page<TopicEntity> databaseResponse = topicRepository.findMostPopular(pageable);

        return databaseResponse.map(topicMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<TopicOutputModel> getSubtopics(long id) {
        List<TopicEntity> databaseResponse = topicRepository.findByParentTopicId(id);
        return databaseResponse.stream()
                .map(topicMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public TopicOutputModel getTopic(long id) {
        Optional<TopicEntity> databaseResponse = topicRepository.findById(id);
        TopicEntity topicEntity = databaseResponse.get();
        return topicMapper.toModel(topicEntity);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<TopicOutputModel> getParentTopics(Long identifier) {
        Optional<TopicEntity> databaseResponse = topicRepository.findById(identifier);

        if (databaseResponse.isEmpty()) {
            throw new TopicNotFoundException(String.format(TOPIC_NOT_FOUND_EXCEPTION_MESSAGE, identifier));
        }

        TopicEntity topic = databaseResponse.get();

        List<TopicEntity> topicsHierarchy = new ArrayList<>(List.of(topic));
        TopicEntity parent = topic.getParentTopic();

        while(parent != null) {
            topicsHierarchy.add(parent);
            parent = parent.getParentTopic();
        }

        List<TopicOutputModel> topicsModelHierarchy = topicsHierarchy.stream()
                .map(topicMapper::toModel)
                .collect(Collectors.toList());

        return Lists.reverse(topicsModelHierarchy);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus delete(long id) {
        Optional<TopicEntity> databaseResponse = topicRepository.findById(id);
        databaseResponse.ifPresent(topicRepository::delete);
        return databaseResponse.isPresent() ? OperationStatus.OK : OperationStatus.NOT_FOUND;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus update(TopicInputModel topicOutputModel) {
        TopicEntity topicEntity = topicMapper.toEntity(topicOutputModel);
        topicRepository.save(topicEntity);
        return OperationStatus.OK;
    }

    private Sort getSortingOrder(TopicSortingOrder sortingOrder) {
        switch (sortingOrder) {
            case ALPHABETICAL:
                return Sort.by("name");
            case REVERSE_ALPHABETICAL:
                return Sort.by("name").descending();
            case NEWEST_FIRST:
                return Sort.by("creationTimestamp").descending();
            default:
                return Sort.by("creationTimestamp");
        }
    }
}
