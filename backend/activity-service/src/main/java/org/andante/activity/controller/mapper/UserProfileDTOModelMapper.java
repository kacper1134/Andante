package org.andante.activity.controller.mapper;

import org.andante.activity.dto.UserProfileDTO;
import org.andante.activity.logic.model.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileDTOModelMapper {

    public UserProfileDTO toDTO(UserProfile profile) {
        return UserProfileDTO.builder()
                .key(profile.getKey())
                .username(profile.getUsername())
                .imageUrl(profile.getImageUrl())
                .observedUsers(profile.getObservedUsers())
                .observingUsers(profile.getObservingUsers())
                .build();
    }

    public UserProfile toModel(UserProfileDTO profile) {
        return UserProfile.builder()
                .key(profile.getKey())
                .username(profile.getUsername())
                .imageUrl(profile.getImageUrl())
                .observedUsers(profile.getObservedUsers())
                .observingUsers(profile.getObservingUsers())
                .build();
    }
}
