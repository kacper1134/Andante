package org.andante.activity.logic;

import org.andante.activity.dto.UserImageDTO;
import org.andante.activity.logic.model.UserProfile;
import org.andante.enums.OperationStatus;

import java.util.List;
import java.util.Set;

public interface UserProfileService {
    UserProfile getUserProfile(String username);
    UserProfile getUserProfile(String userId, String userName);
    Set<UserProfile> getObservers(String username);
    Set<UserProfile> getObserving(String username);
    OperationStatus changeObservationStatus(String observedUsername, String observerUsername);
    UserProfile setUserImage(String userId, String userName, String imageUrl);
    List<UserImageDTO> getUsersImage(List<String> userNames);
    UserImageDTO getUserCommunityImage(String username);
    UserProfile setUserCommunityImage(String userId, String userName, String imageUrl);
}
