package org.andante.activity.logic;

import org.andante.activity.dto.ActivityQuerySpecification;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationStatus;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface ActivityService {
    Activity get(String id);
    Page<Activity> getAffectingAll(Integer page, Integer size);
    Page<Activity> getByUser(String user, Integer page, Integer size);
    Page<Activity> getByUserAcknowledged(String user, Integer page, Integer size);
    Page<Activity> getByUserNotAcknowledged(String user, Integer page, Integer size);
    Set<Activity> getAll(Set<String> ids);
    Page<Activity> getByQuery(ActivityQuerySpecification activityQuerySpecification);
    Activity create(Activity activity);
    Activity modify(Activity activity);
    Activity delete(String key);
    OperationStatus markAsViewed(String key, String emailAddress);
}
