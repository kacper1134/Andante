package org.andante.product.repository.entity;

import lombok.*;
import org.andante.product.logic.model.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name="username")
    private String username;

    @Column(nullable = false)
    private Float rating;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private ProductEntity product;

    @Column(name="product_id", nullable = false, insertable = false, updatable = false)
    private Long productId;

    @ElementCollection
    @CollectionTable(name="UsersToComments", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "observer")
    private Set<String> observers;

    @CreatedDate
    @Column(name = "created", updatable = false)
    private LocalDateTime creationTimestamp;

    @LastModifiedDate
    @Column(name = "last_modified")
    private LocalDateTime localDateTime;

    public Comment toModel() {
        return Comment.builder()
                .id(id)
                .creationTimestamp(creationTimestamp)
                .username(username)
                .rating(rating)
                .title(title)
                .content(content)
                .productId(product.getId())
                .productName(product.getName())
                .observers(observers)
                .build();
    }
}
