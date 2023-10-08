package org.andante.activity.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.activity.logic.model.UserProfile;
import org.andante.activity.repository.UserProfileRepository;
import org.andante.activity.repository.entity.UserProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserProfileModelEntityMapper {

    private final UserProfileRepository userProfileRepository;

    public UserProfile toModel(UserProfileEntity userProfileEntity) {
        return UserProfile.builder()
                .key(userProfileEntity.getId())
                .username(userProfileEntity.getUsername())
                .imageUrl(userProfileEntity.getImageUrl())
                .communityImageUrl(userProfileEntity.getCommunityImageUrl())
                .observingUsers(userProfileEntity.getObservers().stream()
                        .map(UserProfileEntity::getUsername)
                        .collect(Collectors.toSet()))
                .observedUsers(userProfileEntity.getObserved().stream()
                        .map(UserProfileEntity::getUsername)
                        .collect(Collectors.toSet()))
                .build();
    }

    public UserProfileEntity toEntity(UserProfile userProfile) {
        Set<UserProfileEntity> observers = new HashSet<>(userProfileRepository.findAllById(userProfile.getObservingUsers()));
        Set<UserProfileEntity> observed = new HashSet<>(userProfileRepository.findAllById(userProfile.getObservedUsers()));

        return UserProfileEntity.builder()
                .id(userProfile.getKey())
                .username(userProfile.getUsername())
                .communityImageUrl(userProfile.getCommunityImageUrl())
                .imageUrl(userProfile.getImageUrl())
                .observers(observers)
                .observed(observed)
                .build();
    }
}
