package org.andante.forum.logic.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class UserModel {

    private String emailAddress;
    private String name;
    private String surname;
    private String username;
    private LocalDateTime createDate;
    private LocalDateTime modificationDate;
    private Set<Long> posts;
    private Set<Long> responses;
    private Set<Long> likedPosts;
    private Set<Long> likedResponses;

}
