package org.andante.forum.repository.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ResponsesToLikes", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "response_id"})})
public class PostResponsesLikesRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @CreatedDate
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @LastModifiedBy
    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(nullable = false, name = "response_id")
    private PostResponseEntity response;

    @PreRemove
    private void removeLikeFromUserAndResponse() {
        Set<PostResponsesLikesRelationEntity> userLikedResponses = user.getLikedResponses();
        if(userLikedResponses != null) {
            userLikedResponses.remove(this);
        }
        Set<PostResponsesLikesRelationEntity> likedByUsers = response.getLikedByUsers();
        if(likedByUsers != null) {
            likedByUsers.remove(this);
        }
    }
}