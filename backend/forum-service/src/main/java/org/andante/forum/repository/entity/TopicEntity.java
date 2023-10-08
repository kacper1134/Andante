package org.andante.forum.repository.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Topics")
public class TopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 250, name = "image_url")
    private String imageUrl;

    @CreatedDate
    private LocalDateTime creationTimestamp;

    @LastModifiedDate
    private LocalDateTime modificationTimestamp;

    @OneToMany(mappedBy = "parentTopic", cascade = CascadeType.ALL)
    private Set<TopicEntity> childTopics;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private TopicEntity parentTopic;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<PostEntity> posts;
}
