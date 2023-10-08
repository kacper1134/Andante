package org.andante.activity.logic.service.impl;

import org.andante.activity.configuration.ActivityTestConfiguration;
import org.andante.activity.dto.ActivityQuerySpecification;
import org.andante.activity.exception.ActivityConflictException;
import org.andante.activity.exception.ActivityNotFoundException;
import org.andante.activity.logic.impl.DefaultActivityService;
import org.andante.activity.logic.mapper.ActivityModelEntityMapper;
import org.andante.activity.logic.model.Activity;
import org.andante.activity.repository.ActivityRepository;
import org.andante.activity.repository.entity.ActivityEntity;
import org.andante.activity.utils.ActivityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(ActivityTestConfiguration.class)
@Transactional
public class DefaultActivityServiceTest {

    @Autowired
    private ActivityTestUtils utils;

    @Autowired
    private DefaultActivityService service;

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private ActivityModelEntityMapper mapper;

    @MockBean
    private JwtDecoder decoder;

    @Nested
    @DisplayName("Get")
    class GetTests {

        @Test
        @DisplayName("should raise ActivityNotFoundException if activity does not exist")
        void shouldRaiseActivityNotFoundExceptionIfActivityDoesNotExist() {
            // given
            String activityId = utils.generate(String.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.get(activityId))
                    .isInstanceOf(ActivityNotFoundException.class);
        }

        @Test
        @DisplayName("should return existing activity")
        void shouldReturnExistingActivity() {
            // given
            ActivityEntity existingActivity = repository.save(utils.generate(ActivityEntity.class));
            Activity expectedResult = mapper.toModel(existingActivity);

            // when
            Activity serviceResponse = service.get(existingActivity.getId());

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get By User")
    class GetByUserTests {

        @Test
        @DisplayName("should return empty set if user does not exist")
        void shouldReturnEmptySetIfUserDoesNotExist() {
            // given
            String user = utils.generate(String.class);

            // when
            Page<Activity> userActivities = service.getByUser(user, 0, 1);

            // then
            assertThat(userActivities).isNotNull();
        }

    }

    @Nested
    @DisplayName("Get All")
    class GetAllTests {

        @Test
        @DisplayName("should return empty set for empty database")
        void shouldReturnEmptySetForEmptyDatabase() {
            // given
            Set<String> identifiers = utils.generate(String.class, 5);

            // when
            Set<Activity> serviceResponse = service.getAll(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all existing activities")
        void shouldReturnAllExistingActivities() {
            // given
            int limit = 3;

            List<ActivityEntity> persistedActivities = repository.saveAll(utils.generate(ActivityEntity.class, 5));

            Set<String> identifiers = persistedActivities.stream()
                    .limit(limit)
                    .map(ActivityEntity::getId)
                    .collect(Collectors.toSet());

            identifiers.addAll(utils.generate(String.class, limit));

            // when
            Set<Activity> serviceResponse = service.getAll(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().hasSize(limit);
        }
    }

    @Nested
    @DisplayName("Get By Query")
    class GetByQueryTests {

        @Test
        @DisplayName("should return all activities matching query")
        void shouldReturnAllActivitiesMatchingQuery() {
            // given
            List<ActivityEntity> existingActivities = repository.saveAll(utils.generate(ActivityEntity.class, 5));

            ActivityQuerySpecification querySpecification = ActivityQuerySpecification.builder()
                    .query("affectsAll==true")
                    .pageNumber(0)
                    .pageSize(5)
                    .build();

            Set<Activity> expectedResult = existingActivities.stream()
                    .filter(ActivityEntity::getAffectsAll)
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            // when
            Page<Activity> serviceResponse = service.getByQuery(querySpecification);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getContent()).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {

        @Test
        @DisplayName("should raise ActivityConflictException when activity with given id already exists")
        void shouldRaiseActivityConflictExceptionWhenActivityWithGivenIdAlreadyExists() {
            // given
            ActivityEntity persistedActivity = repository.save(utils.generate(ActivityEntity.class));

            Activity activityToPersist = mapper.toModel(persistedActivity);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.create(activityToPersist))
                    .isInstanceOf(ActivityConflictException.class);
        }

        @Test
        @DisplayName("should create correct activity model")
        void shouldCreateCorrectActivityModel() {
            // given
            Activity activityToPersist = utils.generate(Activity.class);

            // when
            Activity serviceResponse = service.create(activityToPersist);
            activityToPersist.setEventTimestamp(serviceResponse.getEventTimestamp());

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(activityToPersist);
        }

    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("should raise ActivityNotFound if activity does not exist")
        void shouldRaiseActivityNotFoundExceptionIfActivityDoesNotExist() {
            // given
            Activity activityToUpdate = utils.generate(Activity.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.modify(activityToUpdate))
                    .isInstanceOf(ActivityNotFoundException.class);
        }

        @Test
        @DisplayName("should update correct activity model")
        void shouldUpdateCorrectActivityModel() {
            // given
            ActivityEntity persistedActivity = repository.save(utils.generate(ActivityEntity.class));

            Activity activityToUpdate = mapper.toModel(persistedActivity);

            // when
            Activity updatedActivity = service.modify(activityToUpdate);

            // then
            assertThat(updatedActivity).isNotNull().isEqualTo(activityToUpdate);
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("should raise ActivityNotFoundException when activity does not exist")
        void shouldRaiseActivityNotFoundExceptionWhenActivityDoesNotExist() {
            // given
            String key = utils.generate(String.class);

            // when
            // always

            // then
            assertThatThrownBy(() -> service.delete(key))
                    .isInstanceOf(ActivityNotFoundException.class);
        }

        @Test
        @DisplayName("should delete existing activity")
        void shouldDeleteExistingActivity() {
            // given
            ActivityEntity activity = repository.save(utils.generate(ActivityEntity.class));
            Activity expectedResult = mapper.toModel(activity);

            // when
            Activity serviceResponse = service.delete(activity.getId());

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
            assertThat(repository.findById(activity.getId())).isEmpty();
        }
    }
}
