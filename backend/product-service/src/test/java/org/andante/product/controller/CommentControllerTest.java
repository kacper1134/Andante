package org.andante.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.product.dto.CommentDTO;
import org.andante.product.enums.CommentSortingOrder;
import org.andante.product.repository.CommentRepository;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestContainersExtension.class)
@Import({AmplifiersTestUtils.class, AmplifiersTestConfiguration.class})
@Transactional
public class CommentControllerTest {

    private static final String FIND_COMMENTS_PATH = "/product/comment/ids?ids=%s";
    private static final String FIND_PRODUCT_COMMENTS_PATH = "/product/comment/all/%s";
    private static final String FIND_BY_QUERY_PATH = "/product/comment/query?query=%s&page=%d&pageSize=%d&sortingOrder=%s";
    private static final String CHANGE_OBSERVATION_STATUS_PATH = "/product/comment/favourite?user=%s&id=%d";
    private static final String CREATE_COMMENT_PATH = "/product/comment";
    private static final String UPDATE_COMMENT_PATH = "/product/comment";
    private static final String DELETE_COMMENT_PATH = "/product/comment/%d";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AmplifiersTestUtils utils;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Find Comments")
    class FindCommentsTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided set of identifiers is empty")
        void shouldReturnPreconditionFailedStatusWhenProvidedSetOfIdentifiersIsEmpty() {
            // given
            String serializedIdentifiers = "";

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_COMMENTS_PATH, serializedIdentifiers))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when provided set of identifiers contains nonpositive values")
        void shouldReturnPreconditionFailedStatusWhenProvidedSetOfIdentifiersContainsNonPositiveValues() {
            // given
            Set<Long> identifiers = Set.of(1L, -2L, 3L, -4L, 5L);
            String serializedIdentifiers = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_COMMENTS_PATH, serializedIdentifiers))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all existing comments with provided identifiers")
        void shouldReturnAllExistingCommentsWithProvidedIdentifiers() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<CommentEntity> comments = utils.generate(CommentEntity.class, 5);
            comments.forEach(comment -> comment.setProduct(persistedAmplifiers));

            List<CommentEntity> persistedComments = commentRepository.saveAll(comments);

            Set<Long> identifiers = persistedComments.stream()
                    .map(CommentEntity::getId)
                    .collect(Collectors.toSet());

            identifiers.add(Math.abs(utils.generate(Long.class)));

            String serializedIdentifiers = identifiers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_COMMENTS_PATH, serializedIdentifiers))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andReturn();

            List<CommentDTO> controllerResponse = readResponse(result);

            // then
            assertThat(controllerResponse).isNotNull().hasSameSizeAs(persistedComments);
        }
    }

    @Nested
    @DisplayName("Find Product Comments")
    class FindProductCommentsTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status when identifier is negative")
        void shouldReturnPreconditionFailedStatusWhenIdentifierIsNegative() {
            // given
            Long identifier = -1L;

            // when
            MvcResult result = mockMvc.perform(get(String.format(FIND_PRODUCT_COMMENTS_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all existing product comments")
        void shouldReturnAllExistingProductComments() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<CommentEntity> comments = utils.generate(CommentEntity.class, 5);
            comments.forEach(comment -> comment.setProduct(persistedAmplifiers));

            commentRepository.saveAll(comments);

            Long identifier = persistedAmplifiers.getId();
            String serializedIdentifier = objectMapper.writeValueAsString(identifier);

            // when
            mockMvc.perform(get(String.format(FIND_PRODUCT_COMMENTS_PATH, serializedIdentifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

        }
    }

    @Nested
    @DisplayName("Find By Query")
    class FindByQueryTests {

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if query is not valid")
        void shouldReturnBadRequestStatusIfQueryIsNotValid() {
            // given
            String query = "";
            int page = -1;
            int pageSize = -1;
            String sortingOrder = "";

            // when
            mockMvc.perform(get(String.format(FIND_BY_QUERY_PATH, query, page, pageSize, sortingOrder))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should return all comments matching provided query")
        void shouldReturnAllCommentsMatchingProvidedQuery() {
            // given
            String query = "id=gt=0";
            int page = 0;
            int pageSize = 3;
            CommentSortingOrder sortingOrder = CommentSortingOrder.HIGHEST_RATING;

            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<CommentEntity> comments = utils.generate(CommentEntity.class, 5);
            comments.forEach(comment -> comment.setProduct(persistedAmplifiers));

            commentRepository.saveAll(comments);

            // when
            mockMvc.perform(get(String.format(FIND_BY_QUERY_PATH, query, page, pageSize, sortingOrder.name()))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Change Observation Status")
    class ChangeObservationStatusTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if provided email is not valid")
        void shouldReturnPreconditionFailedIfProvidedEmailIsNotValid() {
            // given
            Long identifier = 1L;
            String observer = utils.generate(String.class);

            // when
            MvcResult result = mockMvc.perform(post(String.format(CHANGE_OBSERVATION_STATUS_PATH, observer, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return not found status if comment does not exist")
        void shouldReturnNotFoundStatusIfCommentDoesNotExist() {
            // given
            Long identifier = Math.abs(utils.generate(Long.class));
            String observer = "test@gmail.com";

            // when
            mockMvc.perform(post(String.format(CHANGE_OBSERVATION_STATUS_PATH, observer, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        @SneakyThrows
        @DisplayName("should add user to observers if he is already not one")
        void shouldAddUserToObserversIfHeIsAlreadyNotOne() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity comment = utils.generate(CommentEntity.class);
            comment.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = commentRepository.save(comment);

            Long identifier = persistedComment.getId();
            String observer = "test@gmail.com";

            // when
            mockMvc.perform(post(String.format(CHANGE_OBSERVATION_STATUS_PATH, observer, identifier))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // then
            Optional<CommentEntity> updatedComment = commentRepository.findById(identifier);

            assertThat(updatedComment).isNotEmpty();
            assertThat(updatedComment.get().getObservers()).contains(observer);
        }

        @Test
        @SneakyThrows
        @DisplayName("should remove user from observers if he is already one")
        void shouldRemoveUserFromObserversIfHeIsAlreadyOne() {
            // given
            String observer = "test@gmail.com";

            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);
            CommentEntity comment = utils.generate(CommentEntity.class);

            comment.setProduct(persistedAmplifiers);
            comment.getObservers().add(observer);

            CommentEntity persistedComment = commentRepository.save(comment);

            Long identifier = persistedComment.getId();

            // when
            mockMvc.perform(post(String.format(CHANGE_OBSERVATION_STATUS_PATH, observer, identifier))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // then
            Optional<CommentEntity> updatedComment = commentRepository.findById(identifier);

            assertThat(updatedComment).isNotEmpty();
            assertThat(updatedComment.get().getObservers()).doesNotContain(observer);
        }
    }

    @Nested
    @DisplayName("Create Comment")
    class CreateCommentTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if comment is not valid")
        void shouldReturnPreconditionFailedStatusIfCommentIsNotValid() {
            // given
            CommentDTO commentToCreate = utils.generate(CommentDTO.class);
            String serializedContent = objectMapper.writeValueAsString(commentToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_COMMENT_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedContent))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if product does not exist")
        void shouldReturnBadRequestStatusIfProductDoesNotExist() {
            // given
            CommentDTO commentToCreate = utils.buildValidComment(Math.abs(utils.generate(Long.class)));
            String serializedComment = objectMapper.writeValueAsString(commentToCreate);

            // when
            mockMvc.perform(post(CREATE_COMMENT_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedComment))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @SneakyThrows
        @DisplayName("should create comment if DTO is valid")
        void shouldCreateCommentIfDTOIsValid() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentDTO commentToCreate = utils.buildValidComment(persistedAmplifiers.getId());

            String serializedComment = objectMapper.writeValueAsString(commentToCreate);

            // when
            MvcResult result = mockMvc.perform(post(CREATE_COMMENT_PATH)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(serializedComment))
                    .andExpect(status().isOk())
                    .andReturn();

            Integer controllerResponse = readResponse(result);

            // then
            Optional<CommentEntity> createdComment = commentRepository.findById((long)controllerResponse);

            assertThat(createdComment).isPresent();
        }
    }

    @Nested
    @DisplayName("Update Comment")
    class UpdateCommentTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if comment is not valid")
        void shouldReturnPreconditionFailedStatusIfCommentIsNotValid() {
            // given
            CommentDTO commentToCreate = utils.generate(CommentDTO.class);
            String serializedContent = objectMapper.writeValueAsString(commentToCreate);

            // when
            MvcResult result = mockMvc.perform(put(UPDATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedContent))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if product does not exist")
        void shouldReturnBadRequestStatusIfProductDoesNotExist() {
            // given
            CommentDTO commentToCreate = utils.buildValidComment(Math.abs(utils.generate(Long.class)));
            String serializedComment = objectMapper.writeValueAsString(commentToCreate);

            // when
            mockMvc.perform(put(UPDATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedComment))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        @SneakyThrows
        @DisplayName("should update comment if DTO is valid")
        void shouldCreateCommentIfDTOIsValid() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity commentToCreate = utils.generate(CommentEntity.class);
            commentToCreate.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = commentRepository.save(commentToCreate);

            CommentDTO commentToUpdate = utils.buildValidComment(persistedAmplifiers.getId());
            commentToUpdate.setId(persistedComment.getId());

            String serializedComment = objectMapper.writeValueAsString(commentToUpdate);

            // when
            mockMvc.perform(put(UPDATE_COMMENT_PATH)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(serializedComment))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Delete Comment")
    class DeleteCommentTests {

        @Test
        @SneakyThrows
        @DisplayName("should return precondition failed status if provided identifier is negative")
        void shouldReturnPreconditionFailedStatusIfProvidedIdentifierIsNegative() {
            // given
            Long identifier = -1L;

            // when
            MvcResult result = mockMvc.perform(delete(String.format(DELETE_COMMENT_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isPreconditionFailed())
                    .andReturn();

            List<String> violationMessages = readResponse(result);

            // then
            assertThat(violationMessages).isNotNull().isNotEmpty();
        }

        @Test
        @SneakyThrows
        @DisplayName("should return bad request status if comment with provided identifier does not exist")
        void shouldReturnBadRequestStatusIfCommentWithProvidedIdentifierDoesNotExist() {
            // given
            Long identifier = Math.abs(utils.generate(Long.class));

            // when
            mockMvc.perform(delete(String.format(DELETE_COMMENT_PATH, identifier))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        @DisplayName("should delete existing comment")
        void shouldDeleteExistingComment() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity commentToCreate = utils.generate(CommentEntity.class);
            commentToCreate.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = commentRepository.save(commentToCreate);

            Long identifier = persistedComment.getId();

            // when
            mockMvc.perform(delete(String.format(DELETE_COMMENT_PATH, identifier))
                    .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());

            // then
            assertThat(commentRepository.findById(identifier)).isEmpty();
        }
    }

    @SneakyThrows
    private <T> T readResponse(MvcResult result) {
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
    }
}
