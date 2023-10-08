package org.andante.activity.controller.mapper;

import org.andante.activity.dto.ActivityDTO;
import org.andante.activity.logic.model.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityDTOModelMapper {

    public ActivityDTO toDTO(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getKey())
                .description(activity.getDescription())
                .affectedUsers(activity.getAffectedUsers())
                .acknowledgedUsers(activity.getAcknowledgedUsers())
                .eventTimestamp(activity.getEventTimestamp())
                .priority(activity.getPriority())
                .domain(activity.getDomain())
                .relatedId(activity.getRelatedId())
                .build();
    }

    public Activity toModel(ActivityDTO activityDTO) {
        return Activity.builder()
                .key(activityDTO.getId())
                .description(activityDTO.getDescription())
                .affectedUsers(activityDTO.getAffectedUsers())
                .acknowledgedUsers(activityDTO.getAcknowledgedUsers())
                .eventTimestamp(activityDTO.getEventTimestamp())
                .priority(activityDTO.getPriority())
                .domain(activityDTO.getDomain())
                .relatedId(activityDTO.getRelatedId())
                .build();
    }
}
