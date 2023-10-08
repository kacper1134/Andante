package org.andante.forum.logic.service;

import dto.topic.TopicQuerySpecification;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.model.topic.TopicInputModel;
import org.andante.forum.logic.model.topic.TopicOutputModel;
import org.springframework.data.domain.Page;

import java.util.Set;
import java.util.List;

public interface TopicService {

    Long create(TopicInputModel topicOutputModel);
    Page<TopicOutputModel> getByQuery(TopicQuerySpecification topicQuerySpecification);
    Page<TopicOutputModel> getTop(Integer page, Integer count);
    Set<TopicOutputModel> getSubtopics(long id);
    List<TopicOutputModel> getParentTopics(Long identifier);
    TopicOutputModel getTopic(long id);
    OperationStatus delete(long id);
    OperationStatus update(TopicInputModel topicOutputModel);
}
