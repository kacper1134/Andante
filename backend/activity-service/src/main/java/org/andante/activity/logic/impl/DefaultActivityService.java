package org.andante.activity.logic.impl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import org.andante.activity.dto.ActivityQuerySpecification;
import org.andante.activity.exception.ActivityConflictException;
import org.andante.activity.exception.ActivityNotFoundException;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.mapper.ActivityModelEntityMapper;
import org.andante.activity.logic.model.Activity;
import org.andante.activity.repository.ActivityRepository;
import org.andante.activity.repository.entity.ActivityEntity;
import org.andante.enums.OperationStatus;
import org.andante.rsql.PersistentRSQLVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultActivityService implements ActivityService {

    private static final String ACTIVITY_NOT_FOUND_EXCEPTION_MESSAGE = "Activity with identifier %s was not found";
    private static final String ACTIVITY_CONFLICT_EXCEPTION_MESSAGE = "Activity with identifier %s already exists";
    private static final String USER_NOT_AFFECTED_EXCEPTION_MESSAGE = "%s is not one of observing users of activity %s";

    private final ActivityRepository activityRepository;
    private final ActivityModelEntityMapper activityModelEntityMapper;
    private final RSQLParser rsqlParser;
    private final PersistentRSQLVisitor<ActivityEntity> rsqlVisitor;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Activity get(String id) {
        Optional<ActivityEntity> databaseResponse = activityRepository.findById(id);

        if (databaseResponse.isEmpty()) {
            throw new ActivityNotFoundException(String.format(ACTIVITY_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        return activityModelEntityMapper.toModel(databaseResponse.get());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<Activity> getAffectingAll(Integer page, Integer count) {
        Pageable pageable = PageRequest.of(page, count, getSortingOrder());

        Page<ActivityEntity> databaseResponse = activityRepository.findByAffectsAllTrue(pageable);

        return databaseResponse.map(activityModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<Activity> getByUser(String user, Integer page, Integer size) {
        Pageable pageRequest = PageRequest.of(page, size, getSortingOrder());

        Page<ActivityEntity> databaseResponse = activityRepository.findAllByAffectedUsersContainingOrAffectsAllTrue(user, pageRequest);

        return databaseResponse.map(activityModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<Activity> getByUserAcknowledged(String user, Integer page, Integer size) {
        Pageable pageRequest = PageRequest.of(page, size, getSortingOrder());

        Page<ActivityEntity> databaseResponse = activityRepository.findAllByAcknowledgedUsersContaining(user, pageRequest);

        return databaseResponse.map(activityModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<Activity> getByUserNotAcknowledged(String user, Integer page, Integer size) {
        Pageable pageRequest = PageRequest.of(page, size, getSortingOrder());

        Page<ActivityEntity> databaseResponse = activityRepository.findAllByAcknowledgedUsersNotContaining(user, pageRequest);

        return databaseResponse.map(activityModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<Activity> getAll(Set<String> ids) {
        List<ActivityEntity> databaseResponse = activityRepository.findAllById(ids);

        return databaseResponse.stream()
                .map(activityModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Page<Activity> getByQuery(ActivityQuerySpecification activityQuerySpecification) {
        Node rootNode = rsqlParser.parse(activityQuerySpecification.getQuery());
        Specification<ActivityEntity> specification = rootNode.accept(rsqlVisitor);
        Pageable pageSpecification = getPageSpecification(activityQuerySpecification);

        Page<ActivityEntity> databaseResponse = activityRepository.findAll(specification, pageSpecification);

        return databaseResponse.map(activityModelEntityMapper::toModel);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Activity create(Activity activity) {
        if (activity.getKey() != null && activityRepository.existsById(activity.getKey())) {
            throw new ActivityConflictException(String.format(ACTIVITY_CONFLICT_EXCEPTION_MESSAGE, activity.getKey()));
        }

        ActivityEntity activityToCreate = activityModelEntityMapper.toEntity(activity);
        ActivityEntity createdActivity = activityRepository.save(activityToCreate);

        return activityModelEntityMapper.toModel(createdActivity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Activity modify(Activity activity) {
        if (activity.getKey() == null || !activityRepository.existsById(activity.getKey())) {
            throw new ActivityNotFoundException(String.format(ACTIVITY_NOT_FOUND_EXCEPTION_MESSAGE, activity.getKey()));
        }

        ActivityEntity activityToModify = activityModelEntityMapper.toEntity(activity);
        ActivityEntity modifiedActivity = activityRepository.save(activityToModify);

        return activityModelEntityMapper.toModel(modifiedActivity);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Activity delete(String key) {
        Optional<ActivityEntity> activityToDelete = activityRepository.findById(key);

        if (activityToDelete.isEmpty()) {
            throw new ActivityNotFoundException(String.format(ACTIVITY_NOT_FOUND_EXCEPTION_MESSAGE, key));
        }

        activityRepository.delete(activityToDelete.get());

        return activityModelEntityMapper.toModel(activityToDelete.get());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus markAsViewed(String key, String emailAddress) {
        Optional<ActivityEntity> databaseResponse = activityRepository.findById(key);

        if (databaseResponse.isEmpty()) {
            return OperationStatus.NOT_FOUND;
        }

        ActivityEntity activityToModify = databaseResponse.get();

        if (!(activityToModify.getAffectedUsers().contains(emailAddress) || activityToModify.getAffectsAll())) {
            throw new ActivityConflictException(String.format(USER_NOT_AFFECTED_EXCEPTION_MESSAGE, emailAddress, activityToModify.getId()));
        }

        if (activityToModify.getAcknowledgedUsers() == null) {
            activityToModify.setAcknowledgedUsers(Set.of(emailAddress));
        } else {
            activityToModify.getAcknowledgedUsers().add(emailAddress);
        }

        activityRepository.save(activityToModify);

        return OperationStatus.OK;
    }

    private Pageable getPageSpecification(ActivityQuerySpecification activityQuerySpecification) {
        return PageRequest.of(activityQuerySpecification.getPageNumber(), activityQuerySpecification.getPageSize(), getSortingOrder());
    }

    private Sort getSortingOrder() {
        return Sort.by("eventTimestamp").descending();
    }
}
