package org.andante.product.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.mappers.OperationHttpStatusMapper;
import org.andante.product.controller.mapper.CommentDTOModelMapper;
import org.andante.product.dto.CommentDTO;
import org.andante.product.dto.CommentQuerySpecification;
import org.andante.product.dto.CommentStatistics;
import org.andante.product.event.CommentEvent;
import org.andante.product.kafka.producer.KafkaProductProducer;
import org.andante.product.logic.model.Comment;
import org.andante.product.logic.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/product/comment")
@Validated
public class CommentController {

    private static final String IDENTIFIERS_ERROR_MESSAGE = "List of provided identifiers must contain have at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String POSITIVE_IDENTIFIER_ERROR_MESSAGE = "Comment identifier '${validatedValue}' must be a positive value";
    private static final String EMAIL_ERROR_MESSAGE = "Provided value '${validatedValue}' must be a valid email address";
    private static final String EMAIL_NULL_ERROR_MESSAGE = "Provided email address must not be a null";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Comment identifier must not be a null";
    private static final String PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE = "Page number '${validatedValue}' must not be a negative number";
    private static final String PAGE_NUMBER_NULL_ERROR_MESSAGE = "Page number must not be null";
    private static final String PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE = "Page size '${validatedValue}' must be a positive number";
    private static final String PAGE_SIZE_NULL_ERROR_MESSAGE = "Page size must not be a null";
    private static final String USERNAME_BLANK_ERROR_MESSAGE = "Username must not be blank";

    private final CommentService commentService;
    private final CommentDTOModelMapper commentDTOModelMapper;
    private final KafkaProductProducer kafkaProductProducer;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping("/ids")
    public ResponseEntity<Set<CommentDTO>> findComments(@RequestParam(name = "ids") @Size(min = 1, message = IDENTIFIERS_ERROR_MESSAGE)
                                                        @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) List<@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                        @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<Comment> serviceResponse = commentService.getComments(identifiers);

        Set<CommentDTO> commentsFound = serviceResponse.stream()
                .map(Comment::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(commentsFound);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Set<CommentDTO>> findProductComments(@PathVariable("id") @Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                                   @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long productId) {
        Set<Comment> serviceResponse = commentService.getProductComments(productId);

        Set<CommentDTO> commentsFound = serviceResponse.stream()
                .map(Comment::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(commentsFound);
    }

    @GetMapping("/observed/{email}")
    public ResponseEntity<Set<CommentDTO>> findObserved(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE)
                                                        @Email(message = EMAIL_ERROR_MESSAGE) @PathVariable("email") String email) {
        Set<Comment> serviceResponse = commentService.getAllObserved(email);

        Set<CommentDTO> observedComments = serviceResponse.stream()
                .map(commentDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(observedComments);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<CommentDTO>> findByQuery(@Valid CommentQuerySpecification commentQuerySpecification) {
        Page<Comment> serviceResponse = commentService.getByQuery(commentQuerySpecification);

        Page<CommentDTO> comments = serviceResponse.map(Comment::toDTO);

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/statistics/{username}")
    public ResponseEntity<CommentStatistics> getUserStatistics(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE)
                                                               @PathVariable("username") String username) {
        CommentStatistics serviceResponse = commentService.getStatistics(username);

        return ResponseEntity.ok(serviceResponse);
    }

    @GetMapping("/top")
    public ResponseEntity<List<CommentDTO>> findTopByUser(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE) @RequestParam(name = "username") String username,
                                                    @PositiveOrZero(message = PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE)
                                                    @NotNull(message = PAGE_NUMBER_NULL_ERROR_MESSAGE) @RequestParam(name = "page") Integer page,
                                                    @Positive(message = PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE)
                                                    @NotNull(message = PAGE_SIZE_NULL_ERROR_MESSAGE) @RequestParam(name = "size") Integer pageSize) {
        List<Comment> serviceResponse = commentService.getTopComments(username, page, pageSize);

        List<CommentDTO> topComments = serviceResponse.stream()
                .map(commentDTOModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topComments);
    }

    @PostMapping("/favourite")
    public ResponseEntity<OperationStatus> changeObservationStatus(@RequestParam("user") @Email(message = EMAIL_ERROR_MESSAGE)
                                                                   @NotNull(message = EMAIL_NULL_ERROR_MESSAGE) String email,
                                                                   @RequestParam("id") @Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                                   @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long id) {
        OperationStatus serviceResponse = commentService.changeObservationStatus(email, id);

        return ResponseEntity.status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }

    @PostMapping
    public ResponseEntity<Long> createComment(@Valid @RequestBody CommentDTO commentDTO) {
        Comment commentToCreate = commentDTOModelMapper.toModel(commentDTO);
        Comment serviceResponse = commentService.createComment(commentToCreate);
        CommentDTO mappedComment = commentDTOModelMapper.toDTO(serviceResponse);

        kafkaProductProducer.publish(CommentEvent.builder()
                .comment(mappedComment)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modifyComment(@Valid @RequestBody CommentDTO commentDTO) {
        Comment commentToModify = commentDTOModelMapper.toModel(commentDTO);
        Comment serviceResponse = commentService.updateComment(commentToModify);
        CommentDTO mappedComment = commentDTOModelMapper.toDTO(serviceResponse);

        kafkaProductProducer.publish(CommentEvent.builder()
                .comment(mappedComment)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> deleteComment(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                         @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long id) {
        Comment serviceResponse = commentService.deleteComment(id);
        CommentDTO commentDTO = commentDTOModelMapper.toDTO(serviceResponse);

        kafkaProductProducer.publish(CommentEvent.builder()
                .comment(commentDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
