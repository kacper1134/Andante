package org.andante.activity.repository.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Newsletters")
public class NewsletterEntity {

    @Id
    @Column(name = "email_address")
    private String emailAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime subscriptionDate;

    @Column(nullable = false, name = "confirmed", columnDefinition = "boolean default false")
    private Boolean isConfirmed;
}
