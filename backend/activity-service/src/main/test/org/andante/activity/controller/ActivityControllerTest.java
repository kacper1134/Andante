package org.andante.activity.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.activity.configuration.ActivityTestConfiguration;
import org.andante.activity.repository.ActivityRepository;
import org.andante.activity.repository.entity.ActivityEntity;
import org.andante.activity.utils.ActivityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({ActivityTestUtils.class, ActivityTestConfiguration.class})
@Transactional
public class ActivityControllerTest {

    private static final String GET_ACTIVITY_PATH = "/activity/%s";
    private static final String GET_FOR_USER_PATH = "/activity/user/%s?page=%d&size=%d";
    private static final String GET_ALL_BY_IDS = "/activity/bulk?ids=%s";
    private static final String GET_BY_QUERY = "/activity/query?query=%s&pageNumber=%d&pageSize=%d";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepository repository;

    @Autowired
    private ActivityTestUtils utils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder decoder;

    @Nested
    @DisplayName("Get")
    class GetTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided key is blank")
        void shouldReturnPreconditionFailedStatusWhenProvidedEmailIsNotValid() {
            // given
            String activityId = " ";

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_ACTIVITY_PATH, activityId))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when activity does not exist")
        void shouldReturnPreconditionFailedStatusWhenActivityDoesNotExist() {
            // given
            String activityId = utils.generate(String.class);

            // when
            mockMvc.perform(get(String.format(GET_ACTIVITY_PATH, activityId))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return existing activity")
        void shouldReturnExistingActivity() {
            // given
            ActivityEntity persistedActivity = repository.save(utils.generate(ActivityEntity.class));

            String identifier = persistedActivity.getId();

            // when
            mockMvc.perform(get(String.format(GET_ACTIVITY_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Get All For User")
    class GetAllForUserTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when email address is not valid")
        void shouldReturnPreconditionFailedStatusWhenEmailAddressInNotValid() {
            // given
            String email = utils.generate(String.class);

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_FOR_USER_PATH, email, 0, 5))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return empty set if user does not exist and no general activities exist")
        void shouldReturnEmptySetIfUserDoesNotExistAndNoGeneralActivitiesExist() {
            // given
            String email = "test@gmail.com";

            // when
            MvcResult result = mockMvc.perform(get(String.format(GET_FOR_USER_PATH, email, 0, 5))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            // then
            assertThat(result.getResponse().getContentAsString()).isNotNull();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all activities for user")
        void shouldReturnAllActivitiesForUser() {
            // given
            String email = "test@gmail.com";

            repository.saveAll(utils.generate(ActivityEntity.class, 5));

            // when
            mockMvc.perform(get(String.format(GET_FOR_USER_PATH, email, 0, 5))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Get All By Ids")
    class GetAllByIdsTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when at least one of identifiers is blank")
        void shouldReturnPreconditionFailedStatusWhenAtLeastOneOfIdentifiersIsBlank() {
            // given
            Set<String> identifiers = Set.of("first", " ", "third");
            String joinedIdentifiers = String.join(",", identifiers);

            // when
            mockMvc.perform(get(String.format(GET_ALL_BY_IDS, joinedIdentifiers))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return ok status when all identifiers are valid")
        void shouldReturnOkStatusWhenAllIdentifiersAreValid() {
            // given
            Set<String> identifiers = Set.of("first", "second", "third");
            String joinedIdentifiers = String.join(",", identifiers);

            // when
            mockMvc.perform(get(String.format(GET_ALL_BY_IDS, joinedIdentifiers))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Get By Query")
    class GetByQueryTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request when query is invalid")
        void shouldReturnBadRequestWhenQueryIsInvalid() {
            // given
            String query = " ";
            int page = -1;
            int pageSize = 0;

            // when
            mockMvc.perform(get(String.format(GET_BY_QUERY, query, page, pageSize))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request when query has invalid rsql syntax")
        void shouldReturnBadRequestWhenQueryHasInvalidRSQLSyntax() {
            // given
            String query = "bad query example";
            int page = 0;
            int pageSize = 5;

            // when
            mockMvc.perform(get(String.format(GET_BY_QUERY, query, page, pageSize))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return activities matching query")
        void shouldReturnActivitiesMatchingQuery() {
            // given
            String query = "affectsAll==true";
            int page = 0;
            int pageSize = 5;

            Set<ActivityEntity> activities = utils.generate(ActivityEntity.class, 5);

            activities.stream()
                    .limit(3)
                    .forEach(activity -> activity.setAffectsAll(true));

            // when
            mockMvc.perform(get(String.format(GET_BY_QUERY, query, page, pageSize))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }
}
