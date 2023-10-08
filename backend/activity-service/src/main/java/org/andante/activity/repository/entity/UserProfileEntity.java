package org.andante.activity.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Profile")
public class UserProfileEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(nullable = false, length = 1000)
    private String communityImageUrl;

    @ManyToMany(mappedBy = "observers")
    private Set<UserProfileEntity> observed;

    @ManyToMany
    private Set<UserProfileEntity> observers;
}
