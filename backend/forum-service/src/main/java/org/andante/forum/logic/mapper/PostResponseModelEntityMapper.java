package org.andante.forum.logic.mapper;

import exception.PostException;
import exception.PostResponseNotFoundException;
import exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.andante.forum.logic.model.response.PostResponseInputModel;
import org.andante.forum.logic.model.response.PostResponseOutputModel;
import org.andante.forum.logic.model.response.PostResponsesLikesRelationModel;
import org.andante.forum.repository.PostRepository;
import org.andante.forum.repository.PostResponseRepository;
import org.andante.forum.repository.PostResponsesLikesRelationRepository;
import org.andante.forum.repository.UserRepository;
import org.andante.forum.repository.entity.PostEntity;
import org.andante.forum.repository.entity.PostResponseEntity;
import org.andante.forum.repository.entity.PostResponsesLikesRelationEntity;
import org.andante.forum.repository.entity.UserEntity;
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
public class PostResponseModelEntityMapper {

    private static final String POST_NOT_FOUND_EXCEPTION_MESSAGE = "Post %d does not exist";
    private static final String RESPONSE_NOT_FOUND_EXCEPTION_MESSAGE = "Response %d does not exist";
    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User %s does not exist";

    private final PostResponseRepository postResponseRepository;
    private final PostResponsesLikesRelationRepository postResponsesLikesRelationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserModelEntityMapper userMapper;

    public PostResponseOutputModel toResponseModel(PostResponseEntity postResponseEntity) {
        Set<Long> likedByUsers = postResponseEntity.getLikedByUsers().stream()
                .map(PostResponsesLikesRelationEntity::getId)
                .collect(Collectors.toSet());

        return PostResponseOutputModel.builder()
                .id(postResponseEntity.getId())
                .content(postResponseEntity.getContent())
                .createDate(postResponseEntity.getCreateDate())
                .modificationDate(postResponseEntity.getModificationDate())
                .post(postResponseEntity.getPost().getId())
                .user(userMapper.toModel(postResponseEntity.getUser()))
                .likedByUsers(postResponseEntity.getLikedByUsers().stream()
                        .map(this::toResponseLikeModel)
                        .collect(Collectors.toSet()))
                .likesAmount((long) likedByUsers.size())
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PostResponseEntity toResponseEntity(PostResponseInputModel postResponseInputModel) {
        Set<PostResponsesLikesRelationEntity> likes;

        Optional<PostEntity> post = postRepository.findById(postResponseInputModel.getPost());
        Optional<UserEntity> user = userRepository.findByEmail(postResponseInputModel.getUser());

        if (postResponseInputModel.getId() != null) {
            likes = new HashSet<>(postResponsesLikesRelationRepository.findByResponseId(postResponseInputModel.getId()));
        } else {
            likes = new HashSet<>(Collections.emptySet());
        }

        if (post.isEmpty()) {
            throw new PostException(String.format(POST_NOT_FOUND_EXCEPTION_MESSAGE, postResponseInputModel.getPost()));
        }

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format(
                    USER_NOT_FOUND_EXCEPTION_MESSAGE,
                    postResponseInputModel.getUser()
            ));
        }

        return PostResponseEntity.builder()
                .id(postResponseInputModel.getId())
                .content(postResponseInputModel.getContent())
                .post(post.get())
                .postId(post.get().getId())
                .user(user.get())
                .likedByUsers(likes)
                .build();
    }

    public PostResponsesLikesRelationModel toResponseLikeModel(
            PostResponsesLikesRelationEntity responsesLikesRelationEntity
    ) {
        return PostResponsesLikesRelationModel.builder()
                .id(responsesLikesRelationEntity.getId())
                .creationDate(responsesLikesRelationEntity.getCreationDate())
                .modificationDate(responsesLikesRelationEntity.getModificationDate())
                .email(responsesLikesRelationEntity.getUser().getEmailAddress())
                .response(responsesLikesRelationEntity.getResponse().getId())
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public PostResponsesLikesRelationEntity toResponseLikeEntity(
            PostResponsesLikesRelationModel responsesLikesRelation
    ) {
        Optional<UserEntity> user = userRepository.findById(responsesLikesRelation.getEmail());
        Optional<PostResponseEntity> response = postResponseRepository.findById(responsesLikesRelation.getResponse());

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format(
                    USER_NOT_FOUND_EXCEPTION_MESSAGE,
                    responsesLikesRelation.getEmail()
            ));
        }

        if (response.isEmpty()) {
            throw new PostResponseNotFoundException(String.format(
                    RESPONSE_NOT_FOUND_EXCEPTION_MESSAGE,
                    responsesLikesRelation.getResponse()
            ));
        }

        return PostResponsesLikesRelationEntity.builder()
                .id(responsesLikesRelation.getId())
                .creationDate(responsesLikesRelation.getCreationDate())
                .modificationDate(responsesLikesRelation.getModificationDate())
                .user(user.get())
                .response(response.get())
                .build();
    }
}
