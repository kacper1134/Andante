package org.andante.product.logic.service.impl;

import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.utils.AmplifiersTestUtils;
import org.andante.enums.OperationStatus;
import org.andante.product.dto.CommentQuerySpecification;
import org.andante.product.enums.CommentSortingOrder;
import org.andante.product.exception.CommentConflictException;
import org.andante.product.exception.CommentNotFoundException;
import org.andante.product.logic.mapper.CommentModelEntityMapper;
import org.andante.product.logic.model.Comment;
import org.andante.product.repository.CommentRepository;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.ProductRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import utility.TestContainersExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(TestContainersExtension.class)
@Import(AmplifiersTestUtils.class)
@Transactional
public class DefaultCommentServiceTest {

    @Autowired
    private AmplifiersTestUtils utils;

    @Autowired
    private DefaultCommentService service;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private AmplifiersRepository amplifiersRepository;

    @Autowired
    private CommentRepository repository;

    @Autowired
    private CommentModelEntityMapper mapper;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("Get Comments")
    class GetCommentsTests {

        @Test
        @DisplayName("should return empty set if database is empty")
        void shouldReturnEmptySetIfDatabaseIsEmpty() {
            // given
            List<Long> identifiers = List.of(Math.abs(utils.generate(Long.class)), Math.abs(utils.generate(Long.class)));

            // when
            Set<Comment> serviceResponse = service.getComments(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all requested existing comments")
        void shouldReturnAllRequestedExistingComments() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<CommentEntity> comments = utils.generate(CommentEntity.class, 5);
            comments.forEach(comment -> comment.setProduct(persistedAmplifiers));

            List<CommentEntity> persistedComments = repository.saveAll(comments);

            List<Long> identifiers = persistedComments.stream()
                    .map(CommentEntity::getId)
                    .collect(Collectors.toList());

            identifiers.addAll(utils.generate(Long.class, 5));

            Set<Comment> expectedResult = persistedComments.stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            // when
            Set<Comment> serviceResponse = service.getComments(identifiers);

            // then
            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get Product Comments")
    class GetProductCommentsTests {

        @Test
        @DisplayName("should return no comments when product does not exist")
        void shouldReturnNoCommentsWhenProductDoesNotExist() {
            // given
            Long productIdentifier = Math.abs(utils.generate(Long.class));

            assertThat(productRepository.findById(productIdentifier)).isEmpty();

            // when
            Set<Comment> serviceResponse = service.getProductComments(productIdentifier);

            // then
            assertThat(serviceResponse).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return all product comments")
        void shouldReturnAllProductComments() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<CommentEntity> comments = utils.generate(CommentEntity.class, 5);
            comments.forEach(comment -> comment.setProduct(persistedAmplifiers));

            repository.saveAll(comments);

            Long identifier = persistedAmplifiers.getId();

            Set<Comment> expectedResult = repository.findAllByProductId(identifier).stream()
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            // when
            Set<Comment> serviceResponse = service.getProductComments(identifier);

            // then
            assertThat(serviceResponse).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Get By Query")
    class GetByQueryTests {

        @Test
        @DisplayName("should return no comments when page number is higher than total number of pages")
        void shouldReturnNoCommentsWhenPageNumberIsHigherThanTotalNumberOfPages() {
            // given
            CommentQuerySpecification commentQuerySpecification = CommentQuerySpecification.builder()
                    .query("id=gt=0")
                    .page(100)
                    .pageSize(100)
                    .sortingOrder(CommentSortingOrder.LOWEST_RATING)
                    .build();

            // when
            Page<Comment> serviceResponse = service.getByQuery(commentQuerySpecification);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getContent()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should return comments sorted according to selected sorting order")
        void shouldReturnCommentsSortedAccordingToSelectedSortingOrder() {
            // given
            String query = "id=gt=0";
            int pageSize = 3;
            int page = 0;
            CommentSortingOrder sortingOrder = CommentSortingOrder.NEWEST_FIRST;

            CommentQuerySpecification querySpecification = CommentQuerySpecification.builder()
                    .query(query)
                    .page(page)
                    .pageSize(pageSize)
                    .sortingOrder(sortingOrder)
                    .build();

            ProducerEntity producer =  producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Set<CommentEntity> comments = utils.generate(CommentEntity.class, 5);
            comments.forEach(comment -> comment.setProduct(persistedAmplifiers));

            List<CommentEntity> persistedComments = repository.saveAll(comments);

            Set<Comment> expectedResult = persistedComments.stream()
                    .sorted((c1, c2) -> c2.getCreationTimestamp().compareTo(c1.getCreationTimestamp()))
                    .limit(pageSize)
                    .map(mapper::toModel)
                    .collect(Collectors.toSet());

            // when
            Page<Comment> serviceResponse = service.getByQuery(querySpecification);

            // then
            assertThat(serviceResponse).isNotNull();
            assertThat(serviceResponse.getContent()).isNotNull().hasSameElementsAs(expectedResult);
        }
    }

    @Nested
    @DisplayName("Change Observation Status")
    class ChangeObservationStatusTests {


        @Test
        @DisplayName("should return not found status if comment with provided id does not exist")
        void shouldReturnNotFoundStatusIfCommentWithProvidedIdDoesNotExist() {
            // given
            Long identifier = utils.generate(Long.class);
            String observer = "test@gmail.com";

            // when
            OperationStatus serviceResponse = service.changeObservationStatus(observer, identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(OperationStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("should add observer if customer is not observing given comment")
        void shouldAddObserverIfCustomerIsNotObservingGivenComment() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity comment = utils.generate(CommentEntity.class);
            comment.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = repository.save(comment);

            Long identifier = persistedComment.getId();
            String observer = "test@gmail.com";

            assertThat(persistedComment.getObservers()).doesNotContain(observer);

            // when
            OperationStatus serviceResponse = service.changeObservationStatus(observer, identifier);

            Optional<CommentEntity> updatedComment = repository.findById(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(OperationStatus.OK);
            assertThat(updatedComment).isPresent();
            assertThat(updatedComment.get().getObservers()).contains(observer);
        }

        @Test
        @DisplayName("should remove observer if customer is observing given comment")
        void shouldRemoveObserverIfCustomerIsObservingGivenComment() {
            // given
            String observer = "test@gmail.com";
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity comment = utils.generate(CommentEntity.class);
            comment.setProduct(persistedAmplifiers);
            comment.setObservers(Set.of(observer));

            CommentEntity persistedComment = repository.save(comment);

            Long identifier = persistedComment.getId();

            assertThat(persistedComment.getObservers()).contains(observer);

            // when
            OperationStatus serviceResponse = service.changeObservationStatus(observer, identifier);

            Optional<CommentEntity> updatedComment = repository.findById(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(OperationStatus.OK);
            assertThat(updatedComment).isPresent();
            assertThat(updatedComment.get().getObservers()).doesNotContain(observer);
        }
    }

    @Nested
    @DisplayName("Create Comment")
    class CreateCommentTests {

        @Test
        @DisplayName("should raise CommentConflictException if comment with given id already exists")
        void shouldRaiseCommentConflictExceptionIfCommentWithGivenIdAlreadyExists() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity comment = utils.generate(CommentEntity.class);
            comment.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = repository.save(comment);

            Comment commentToCreate = mapper.toModel(persistedComment);

            assertThat(repository.findById(commentToCreate.getId())).isPresent();

            // when
            // always

            // then
            assertThatThrownBy(() -> service.createComment(commentToCreate))
                    .isInstanceOf(CommentConflictException.class)
                    .hasMessageContaining(String.valueOf(commentToCreate.getId()));
        }

        @Test
        @DisplayName("should create comment if all conditions are met")
        void shouldCreateCommentIfAllConditionsAreMet() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            Comment commentToCreate = utils.generate(Comment.class);
            commentToCreate.setProductId(persistedAmplifiers.getId());

            // when
            Comment createdComment = service.createComment(commentToCreate);

            commentToCreate.setId(createdComment.getId());
            commentToCreate.setProductName(createdComment.getProductName());

            // then
            assertThat(createdComment).isNotNull();
        }
    }

    @Nested
    @DisplayName("Update Comment")
    class UpdateCommentTests {

        @Test
        @DisplayName("should raise CommentNotFoundException if comment to update does not exist")
        void shouldRaiseCommentNotFoundExceptionIfCommentToUpdateDoesNotExist() {
            // given
            Comment commentToUpdate = utils.generate(Comment.class);

            assertThat(repository.findById(commentToUpdate.getId())).isEmpty();
            // when
            // always

            // then
            assertThatThrownBy(() -> service.updateComment(commentToUpdate))
                    .isInstanceOf(CommentNotFoundException.class)
                    .hasMessageContaining(String.valueOf(commentToUpdate.getId()));
        }

        @Test
        @DisplayName("should update comment if it exists")
        void shouldUpdateCommentIfItExists() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity comment = utils.generate(CommentEntity.class);
            comment.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = repository.save(comment);

            Comment commentToUpdate = mapper.toModel(persistedComment);

            // when
            Comment updatedComment = service.updateComment(commentToUpdate);

            // then
            assertThat(updatedComment).isNotNull();
        }
    }

    @Nested
    @DisplayName("Delete Comment")
    class DeleteCommentTests {

        @Test
        @DisplayName("should raise CommentNotFoundException if comment with given id does not exist")
        void shouldRaiseCommentNotFoundExceptionWhenCommentWithGivenIdDoesNotExist() {
            // given
            Long identifier = utils.generate(Long.class);

            assertThat(repository.findById(identifier)).isEmpty();

            // when
            // always

            // then
            assertThatThrownBy(() -> service.deleteComment(identifier))
                    .isInstanceOf(CommentNotFoundException.class)
                    .hasMessageContaining(String.valueOf(identifier));
        }

        @Test
        @DisplayName("should delete comment if it exists")
        void shouldDeleteCommentIfItExists() {
            // given
            ProducerEntity producer = producerRepository.save(utils.generate(ProducerEntity.class));

            AmplifiersEntity amplifiers = utils.generateAmplifier();
            amplifiers.setProducer(producer);

            AmplifiersEntity persistedAmplifiers = amplifiersRepository.save(amplifiers);

            CommentEntity comment = utils.generate(CommentEntity.class);
            comment.setProduct(persistedAmplifiers);

            CommentEntity persistedComment = repository.save(comment);

            Comment expectedResult = mapper.toModel(persistedComment);

            Long identifier = persistedComment.getId();

            // when
            Comment serviceResponse = service.deleteComment(identifier);

            // then
            assertThat(serviceResponse).isNotNull().isEqualTo(expectedResult);
            assertThat(repository.findById(identifier)).isEmpty();
        }
    }
}
