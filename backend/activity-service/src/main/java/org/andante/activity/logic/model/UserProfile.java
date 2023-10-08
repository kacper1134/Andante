package org.andante.activity.logic.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class UserProfile {
    private String key;
    private String username;
    private String imageUrl;
    private String communityImageUrl;
    private Set<String> observedUsers;
    private Set<String> observingUsers;
}
