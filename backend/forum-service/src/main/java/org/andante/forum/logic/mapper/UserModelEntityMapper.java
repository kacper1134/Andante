package org.andante.forum.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.forum.logic.model.UserModel;
import org.andante.forum.repository.PostLikesRelationRepository;
import org.andante.forum.repository.PostRepository;
import org.andante.forum.repository.PostResponseRepository;
import org.andante.forum.repository.PostResponsesLikesRelationRepository;
import org.andante.forum.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserModelEntityMapper {

    private final PostRepository postRepository;
    private final PostResponseRepository postResponseRepository;
    private final PostLikesRelationRepository postLikesRelationRepository;
    private final PostResponsesLikesRelationRepository postResponsesLikesRelationRepository;

    public UserModel toModel(UserEntity userEntity) {
        return UserModel.builder()
                .emailAddress(userEntity.getEmailAddress())
                .name(userEntity.getName())
                .surname(userEntity.getSurname())
                .username(userEntity.getUsername())
                .createDate(userEntity.getCreateDate())
                .modificationDate(userEntity.getModificationDate())
                .posts(userEntity.getPosts().stream()
                        .map(PostEntity::getId)
                        .collect(Collectors.toSet()))
                .responses(userEntity.getResponses().stream()
                        .map(PostResponseEntity::getId)
                        .collect(Collectors.toSet()))
                .likedPosts(userEntity.getLikedPosts().stream()
                        .map(PostLikesRelationEntity::getId)
                        .collect(Collectors.toSet()))
                .likedResponses(userEntity.getLikedResponses().stream()
                        .map(PostResponsesLikesRelationEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public UserEntity toEntity(UserModel user) {
        Set<PostEntity> posts;
        Set<PostResponseEntity> postResponses;
        Set<PostLikesRelationEntity> postLikesRelations;
        Set<PostResponsesLikesRelationEntity> postResponsesLikesRelations;

        if (user.getPosts() != null) {
            posts = new HashSet<>(postRepository.findAllById(user.getPosts()));
        } else {
            posts = new HashSet<>(Collections.emptySet());
        }

        if (user.getResponses() == null) {
            postResponses = new HashSet<>(postResponseRepository.findAllById(user.getResponses()));
        } else {
            postResponses = new HashSet<>(Collections.emptySet());
        }

        if (user.getLikedPosts() == null) {
            postLikesRelations = new HashSet<>(postLikesRelationRepository.findAllById(user.getLikedPosts()));
        } else {
            postLikesRelations = new HashSet<>(Collections.emptySet());
        }

        if (user.getLikedResponses() == null) {
            postResponsesLikesRelations =
                    new HashSet<>(postResponsesLikesRelationRepository.findAllById(user.getLikedResponses()));
        } else {
            postResponsesLikesRelations = new HashSet<>(Collections.emptySet());
        }

        return UserEntity.builder()
                .emailAddress(user.getEmailAddress())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .posts(posts)
                .responses(postResponses)
                .likedPosts(postLikesRelations)
                .likedResponses(postResponsesLikesRelations)
                .build();
    }
}
