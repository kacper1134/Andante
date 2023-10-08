package org.andante.activity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class UserProfileDTO {
    private String key;
    private String username;
    private String imageUrl;
    private Set<String> observedUsers;
    private Set<String> observingUsers;
}
