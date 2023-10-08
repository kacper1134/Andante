package org.andante.forum.logic.mapper;

import exception.PostNotFoundException;
import exception.TopicNotFoundException;
import exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.andante.forum.logic.model.post.PostInputModel;
import org.andante.forum.logic.model.post.PostLikesRelationModel;
import org.andante.forum.logic.model.post.PostOutputModel;
import org.andante.forum.repository.*;
import org.andante.forum.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PostModelEntityMapper {

    private static final String TOPIC_NOT_FOUND_EXCEPTION_MESSAGE = "Topic %d does not exist";
    private static final String POST_NOT_FOUND_EXCEPTION_MESSAGE = "Post %d does not exist";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User %s does not exist";

    private final PostRepository postRepository;
    private final PostLikesRelationRepository postLikesRelationRepository;
    private final PostResponseRepository postResponseRepository;
    private final TopicRepository topicRepository;
    private final TopicModelEntityMapper topicMapper;
    private final UserRepository userRepository;
    private final UserModelEntityMapper userMapper;

    public PostOutputModel toPostOutputModel(PostEntity postEntity) {
        Set<Long> responses = postEntity.getResponses().stream()
                .map(PostResponseEntity::getId)
                .collect(Collectors.toSet());

        Set<PostLikesRelationModel> likes = postEntity.getUserLikes().stream()
                .map(this::toPostLikesModel)
                .collect(Collectors.toSet());

        return PostOutputModel.builder()
                .id(postEntity.getId())
                .title(postEntity.getTitle())
                .content(postEntity.getContent())
                .creationTimestamp(postEntity.getCreationTimestamp())
                .modificationTimestamp(postEntity.getModificationTimestamp())
                .topic(topicMapper.toModel(postEntity.getTopic()))
                .user(userMapper.toModel(postEntity.getUser()))
                .responses(responses)
                .userLikes(likes)
                .responsesAmount(responses.size())
                .likesAmount(likes.size())
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PostEntity toPostEntity(PostInputModel postModel) {
        Set<PostResponseEntity> responses;
        Set<PostLikesRelationEntity> likes;

        Optional<TopicEntity> topic = topicRepository.findById(postModel.getTopic());
        Optional<UserEntity> user = userRepository.findById(postModel.getUser());

        if (postModel.getId() != null) {
            responses = new HashSet<>(postResponseRepository.findByPostId(postModel.getId()));
            likes = new HashSet<>(postLikesRelationRepository.findByPostId(postModel.getId()));
        } else {
            responses = new HashSet<>(Collections.emptySet());
            likes = new HashSet<>(Collections.emptySet());
        }

        if (topic.isEmpty()) {
            throw new TopicNotFoundException(String.format(TOPIC_NOT_FOUND_EXCEPTION_MESSAGE, postModel.getTopic()));
        }

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, postModel.getUser()));
        }

        return PostEntity.builder()
                .id(postModel.getId())
                .title(postModel.getTitle())
                .content(postModel.getContent())
                .topic(topic.get())
                .topicId(topic.get().getId())
                .user(user.get())
                .responses(responses)
                .userLikes(likes)
                .build();
    }

    public PostLikesRelationModel toPostLikesModel(PostLikesRelationEntity postLikesRelationEntity) {
        return PostLikesRelationModel.builder()
                .id(postLikesRelationEntity.getId())
                .creationDate(postLikesRelationEntity.getCreationDate())
                .modificationDate(postLikesRelationEntity.getModificationDate())
                .user(postLikesRelationEntity.getUser().getEmailAddress())
                .post(postLikesRelationEntity.getPost().getId())
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PostLikesRelationEntity toPostLikesEntity(PostLikesRelationModel postLikesRelationModel) {
        Optional<PostEntity> post = postRepository.findById(postLikesRelationModel.getPost());
        Optional<UserEntity> user = userRepository.findById(postLikesRelationModel.getUser());

        if (post.isEmpty()) {
            throw new PostNotFoundException(String.format(
                    POST_NOT_FOUND_EXCEPTION_MESSAGE,
                    postLikesRelationModel.getPost()
            ));
        }

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format(
                    USER_NOT_FOUND_EXCEPTION_MESSAGE,
                    postLikesRelationModel.getUser()
            ));
        }

        return PostLikesRelationEntity.builder()
                .id(postLikesRelationModel.getId())
                .user(user.get())
                .post(post.get())
                .build();
    }
}
