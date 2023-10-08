package org.andante.activity.repository.entity;

import lombok.*;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.springframework.data.annotation.CreatedDate;
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
@Table(name = "Activities")
public class ActivityEntity {

    @Id
    private String id;

    @ElementCollection
    @CollectionTable(name = "AffectedUsers", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "affected_user")
    private Set<String> affectedUsers;

    @ElementCollection
    @CollectionTable(name = "AcknowledgedUsers", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "acknowledged_user")
    private Set<String> acknowledgedUsers;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Domain domain;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private String relatedId;

    @Column(columnDefinition = "boolean default false")
    private Boolean affectsAll;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime eventTimestamp;
}
