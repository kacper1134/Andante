package org.andante.activity.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.activity.logic.model.Activity;
import org.andante.activity.repository.entity.ActivityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ActivityModelEntityMapper {

    public Activity toModel(ActivityEntity activity) {
        return Activity.builder()
                .key(activity.getId())
                .description(activity.getDescription())
                .affectedUsers(activity.getAffectedUsers())
                .acknowledgedUsers(activity.getAcknowledgedUsers())
                .eventTimestamp(activity.getEventTimestamp())
                .priority(activity.getPriority())
                .domain(activity.getDomain())
                .relatedId(activity.getRelatedId())
                .affectsAll(activity.getAffectsAll())
                .build();
    }

    public ActivityEntity toEntity(Activity activity) {
        return ActivityEntity.builder()
                .id(activity.getKey())
                .description(activity.getDescription())
                .affectedUsers(activity.getAffectedUsers())
                .eventTimestamp(activity.getEventTimestamp())
                .priority(activity.getPriority())
                .domain(activity.getDomain())
                .relatedId(activity.getRelatedId())
                .affectsAll(activity.getAffectsAll())
                .acknowledgedUsers(activity.getAcknowledgedUsers())
                .build();
    }
}
