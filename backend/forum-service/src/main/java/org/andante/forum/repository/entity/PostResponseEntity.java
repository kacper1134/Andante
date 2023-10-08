package org.andante.forum.repository.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
@Table(name = "Replies")
public class PostResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String content;

    @CreatedDate
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @ManyToOne
    @JoinColumn(name = "parent_post", nullable = false)
    private PostEntity post;

    @Column(name = "parent_post", nullable = false, insertable = false, updatable = false)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "creator", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "response", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostResponsesLikesRelationEntity> likedByUsers;
}
