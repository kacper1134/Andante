package org.andante.forum.logic.mapper;

import exception.TopicNotFoundException;
import lombok.RequiredArgsConstructor;
import org.andante.forum.logic.model.topic.TopicInputModel;
import org.andante.forum.logic.model.topic.TopicOutputModel;
import org.andante.forum.repository.PostRepository;
import org.andante.forum.repository.TopicRepository;
import org.andante.forum.repository.entity.PostEntity;
import org.andante.forum.repository.entity.TopicEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TopicModelEntityMapper {

    private static final String TOPIC_NOT_FOUND_EXCEPTION_MESSAGE = "Topic %d does not exist";

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    public TopicOutputModel toModel(TopicEntity topicEntity) {
        Long parent;

        if (topicEntity.getParentTopic() != null) {
            parent = topicEntity.getParentTopic().getId();
        } else {
            parent = null;
        }

        int topicPostsAmount = topicEntity.getChildTopics().size();
        List<Integer> childrenPostAmount = topicEntity.getChildTopics().stream()
                .map(this::countChildren)
                .collect(Collectors.toList());

        for (Integer postsAmount : childrenPostAmount) {
            topicPostsAmount = topicPostsAmount + postsAmount;
        }

        return TopicOutputModel.builder()
                .id(topicEntity.getId())
                .name(topicEntity.getName())
                .imageUrl(topicEntity.getImageUrl())
                .creationTimestamp(topicEntity.getCreationTimestamp())
                .modificationTimestamp(topicEntity.getModificationTimestamp())
                .childTopics(topicEntity.getChildTopics().stream()
                        .map(this::toModel)
                        .collect(Collectors.toSet()))
                .parentTopic(parent)
                .posts(topicEntity.getPosts().stream()
                        .map(PostEntity::getId)
                        .collect(Collectors.toSet()))
                .postsAmount((long) topicPostsAmount)
                .build();
    }

    private int countChildren(TopicEntity topicEntity) {
        return topicEntity.getPosts().size();
    }

    public TopicEntity toEntity(TopicInputModel topic) {
        Long topicId = topic.getId();
        Long parentId = topic.getParentTopic();

        Set<TopicEntity> children;
        Set<PostEntity> posts;
        TopicEntity parent;

        if (topicId != null) {
            children = new HashSet<>(topicRepository.findByParentTopicId(topic.getId()));
            posts = new HashSet<>(postRepository.findAllByTopicId(topic.getId()));
        } else {
            children = new HashSet<>(Collections.emptySet());
            posts = new HashSet<>(Collections.emptySet());
        }

        if (parentId != null) {
            Optional<TopicEntity> databaseResponse = topicRepository.findById(topic.getParentTopic());

            if(databaseResponse.isEmpty()) {
                throw new TopicNotFoundException(String.format(TOPIC_NOT_FOUND_EXCEPTION_MESSAGE, parentId));
            }

            parent = databaseResponse.get();
        } else {
            parent = null;
        }

        return TopicEntity.builder()
                .id(topic.getId())
                .name(topic.getName())
                .imageUrl(topic.getImageUrl())
                .childTopics(children)
                .parentTopic(parent)
                .posts(posts)
                .build();
    }
}
