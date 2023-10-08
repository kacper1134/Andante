package org.andante.activity.logic.model;

import lombok.Builder;
import lombok.Data;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
public class Activity {

    private String key;
    private Set<String> affectedUsers;
    private Set<String> acknowledgedUsers;
    private String description;
    private String relatedId;
    private Domain domain;
    private Priority priority;
    @Builder.Default
    private Boolean affectsAll = false;
    private LocalDateTime eventTimestamp;
}
