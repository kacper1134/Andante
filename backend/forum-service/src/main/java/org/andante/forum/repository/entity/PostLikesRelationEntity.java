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
@Table(name = "PostsToLikes", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "post_id"})})
public class PostLikesRelationEntity {

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

    @Column(name = "user_id", nullable = false, updatable = false, insertable = false)
    private String emailAddress;

    @ManyToOne
    @JoinColumn(nullable = false, name = "post_id")
    private PostEntity post;

    @PreRemove
    private void removeLikeFromUserAndPost() {
        Set<PostLikesRelationEntity> userLikedPosts = user.getLikedPosts();
        if(userLikedPosts != null) {
            userLikedPosts.remove(this);
        }
        Set<PostLikesRelationEntity> likedByUsers = post.getUserLikes();
        if(likedByUsers != null) {
            likedByUsers.remove(this);
        }
    }
}
