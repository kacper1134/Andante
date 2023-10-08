package org.andante.activity.controller;

import lombok.RequiredArgsConstructor;
import org.andante.activity.controller.mapper.ActivityDTOModelMapper;
import org.andante.activity.dto.ActivityDTO;
import org.andante.activity.dto.ActivityQuerySpecification;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationStatus;
import org.andante.mappers.OperationHttpStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/activity")
@Validated
public class ActivityController {

    private static final String IDENTIFIERS_LIST_MESSAGE = "Activities identifiers list must contain at least {min} element{s}";
    private static final String IDENTIFIERS_LIST_NULL_MESSAGE = "Activities identifiers list must not be a null";
    private static final String ACTIVITY_IDENTIFIER_NOT_BLANK_MESSAGE = "Activity identifier '${validatedValue}' must not be blank";
    private static final String ACTIVITY_EMAIL_NOT_VALID_MESSAGE = "User email address '${validatedValue}' is not valid";
    private static final String ACTIVITY_EMAIL_BLANK_MESSAGE = "User email address must not be blank";
    private static final String NEGATIVE_PAGE_ERROR_MESSAGE = "Page number '${validatedValue}' must not be negative";
    private static final String NULL_PAGE_ERROR_MESSAGE = "Page number must not be null";
    private static final String NON_POSITIVE_PAGE_SIZE_MESSAGE = "Page size '${validatedValue}' must be positive";
    private static final String NULL_PAGE_SIZE_ERROR_MESSAGE = "Page size must not be null";

    private final ActivityService activityService;
    private final ActivityDTOModelMapper activityDTOModelMapper;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> get(@NotBlank(message = ACTIVITY_IDENTIFIER_NOT_BLANK_MESSAGE) @PathVariable("id") String id) {
        ActivityDTO serviceResponse = activityDTOModelMapper.toDTO(activityService.get(id));

        return ResponseEntity.ok(serviceResponse);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<Page<ActivityDTO>> getByUser(@Email(message = ACTIVITY_EMAIL_NOT_VALID_MESSAGE) @PathVariable("email")
                                                          @NotBlank(message = ACTIVITY_EMAIL_BLANK_MESSAGE) String email,
                                                          @PositiveOrZero(message = NEGATIVE_PAGE_ERROR_MESSAGE) @NotNull(message = NULL_PAGE_ERROR_MESSAGE) @RequestParam("page") Integer page,
                                                          @Positive(message = NON_POSITIVE_PAGE_SIZE_MESSAGE) @NotNull(message = NULL_PAGE_SIZE_ERROR_MESSAGE) @RequestParam("size") Integer size) {
        Page<Activity> serviceResponse = activityService.getByUser(email, page, size);

        Page<ActivityDTO> activities = serviceResponse.map(activityDTOModelMapper::toDTO);

        return ResponseEntity.ok(activities);
    }

    @GetMapping("/user/unread/{email}")
    public ResponseEntity<Page<ActivityDTO>> getByUserNotAcknowledged(@Email(message = ACTIVITY_EMAIL_NOT_VALID_MESSAGE) @PathVariable("email")
                                                                   @NotBlank(message = ACTIVITY_EMAIL_BLANK_MESSAGE) String email,
                                                                   @PositiveOrZero(message = NEGATIVE_PAGE_ERROR_MESSAGE) @NotNull(message = NULL_PAGE_ERROR_MESSAGE) @RequestParam("page") Integer page,
                                                                   @Positive(message = NON_POSITIVE_PAGE_SIZE_MESSAGE) @NotNull(message = NULL_PAGE_SIZE_ERROR_MESSAGE) @RequestParam("size") Integer size) {
        Page<Activity> serviceResponse = activityService.getByUserNotAcknowledged(email, page, size);

        Page<ActivityDTO> activities = serviceResponse.map(activityDTOModelMapper::toDTO);

        return ResponseEntity.ok(activities);
    }

    @GetMapping("/user/acknowledged/{email}")
    public ResponseEntity<Page<ActivityDTO>> getByUserAcknowledged(@Email(message = ACTIVITY_EMAIL_NOT_VALID_MESSAGE) @PathVariable("email")
                                                                   @NotBlank(message = ACTIVITY_EMAIL_BLANK_MESSAGE) String email,
                                                                   @PositiveOrZero(message = NEGATIVE_PAGE_ERROR_MESSAGE) @NotNull(message = NULL_PAGE_ERROR_MESSAGE) @RequestParam("page") Integer page,
                                                                   @Positive(message = NON_POSITIVE_PAGE_SIZE_MESSAGE) @NotNull(message = NULL_PAGE_SIZE_ERROR_MESSAGE) @RequestParam("size") Integer size) {
        Page<Activity> serviceResponse = activityService.getByUserAcknowledged(email, page, size);

        Page<ActivityDTO> activities = serviceResponse.map(activityDTOModelMapper::toDTO);

        return ResponseEntity.ok(activities);
    }

    @GetMapping("/bulk")
    public ResponseEntity<Set<ActivityDTO>> getAllByIds(@Size(min = 1, message = IDENTIFIERS_LIST_MESSAGE) @RequestParam("ids")
                                                        @NotNull(message = IDENTIFIERS_LIST_NULL_MESSAGE) Set<@NotBlank(message = ACTIVITY_IDENTIFIER_NOT_BLANK_MESSAGE) String> ids) {
        Set<Activity> serviceResponse = activityService.getAll(ids);

        Set<ActivityDTO> activities = serviceResponse.stream()
                .map(activityDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(activities);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<ActivityDTO>> getByQuery(@Valid ActivityQuerySpecification activityQuerySpecification) {
        Page<Activity> serviceResponse = activityService.getByQuery(activityQuerySpecification);

        Page<ActivityDTO> activities = serviceResponse.map(activityDTOModelMapper::toDTO);


        return ResponseEntity.ok(activities);
    }

    @GetMapping("/general")
    public ResponseEntity<Page<ActivityDTO>> getAffectingAll(@PositiveOrZero(message = NEGATIVE_PAGE_ERROR_MESSAGE) @NotNull(message = NULL_PAGE_ERROR_MESSAGE) @RequestParam("page") Integer page,
                                                             @Positive(message = NON_POSITIVE_PAGE_SIZE_MESSAGE) @NotNull(message = NULL_PAGE_SIZE_ERROR_MESSAGE) @RequestParam("size") Integer size) {
        Page<Activity> serviceResponse = activityService.getAffectingAll(page, size);

        Page<ActivityDTO> activities = serviceResponse.map(activityDTOModelMapper::toDTO);

        return ResponseEntity.ok(activities);
    }

    @PostMapping("/viewed")
    public ResponseEntity<OperationStatus> markAsViewed(@NotBlank(message = ACTIVITY_IDENTIFIER_NOT_BLANK_MESSAGE) @RequestParam(name = "id") String key,
                                                        @Email(message = ACTIVITY_EMAIL_NOT_VALID_MESSAGE) @RequestParam("email")
                                                        @NotBlank(message = ACTIVITY_EMAIL_BLANK_MESSAGE) String email) {
        OperationStatus serviceResponse = activityService.markAsViewed(key, email);

        return ResponseEntity.status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }
}
