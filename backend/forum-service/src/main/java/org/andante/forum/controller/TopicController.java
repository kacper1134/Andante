package org.andante.forum.controller;

import dto.topic.TopicInputDTO;
import dto.topic.TopicOutputDTO;
import dto.topic.TopicQuerySpecification;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.controller.mapper.TopicDTOModelMapper;
import org.andante.forum.logic.model.topic.TopicInputModel;
import org.andante.forum.logic.model.topic.TopicOutputModel;
import org.andante.forum.logic.service.impl.TopicServiceImpl;
import org.andante.mappers.OperationHttpStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/forum/topic")
@Validated
public class TopicController {

    private static final String IDENTIFIER_ERROR_MESSAGE = "Product identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Product identifier must not be a null";

    private final TopicServiceImpl topicService;
    private final TopicDTOModelMapper topicDTOModelMapper;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping
    public ResponseEntity<TopicOutputDTO> getTopic(
            @RequestParam("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        TopicOutputModel topicOutputModel = topicService.getTopic(id);
        TopicOutputDTO topicOutputDTO = topicDTOModelMapper.toDTO(topicOutputModel);
        return ResponseEntity
                .ok()
                .body(topicOutputDTO);
    }

    @GetMapping("/subtopics")
    public ResponseEntity<Set<TopicOutputDTO>> getSubtopics(
            @RequestParam("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        Set<TopicOutputModel> topicOutputModels = topicService.getSubtopics(id);
        Set<TopicOutputDTO> topicOutputDTOs = topicOutputModels.stream()
                .map(topicDTOModelMapper::toDTO)
                .collect(Collectors.toSet());
        return ResponseEntity
                .ok()
                .body(topicOutputDTOs);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TopicOutputDTO>> getPage(@Valid TopicQuerySpecification topicQuerySpecification) {
        Page<TopicOutputModel> topicOutputModels = topicService.getByQuery(topicQuerySpecification);
        Page<TopicOutputDTO> topicOutputDTOs = topicOutputModels.map(topicDTOModelMapper::toDTO);
        return ResponseEntity.ok().body(topicOutputDTOs);
    }

    @GetMapping("/top")
    public ResponseEntity<Page<TopicOutputDTO>> getTop(@RequestParam("page") Integer page,
                                                       @RequestParam("count") Integer count) {
        Page<TopicOutputModel> serviceResponse = topicService.getTop(page, count);

        Page<TopicOutputDTO> mostPopular = serviceResponse.map(topicDTOModelMapper::toDTO);

        return ResponseEntity.ok(mostPopular);
    }

    @GetMapping("/hierarchy/{id}")
    public ResponseEntity<List<TopicOutputDTO>> getHierarchy(@PathVariable("id") Long identifier) {
        List<TopicOutputModel> serviceResponse = topicService.getParentTopics(identifier);

        List<TopicOutputDTO> topicsHierarchy = serviceResponse.stream()
                .map(topicDTOModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topicsHierarchy);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody TopicInputDTO topicInputDTO) {
        TopicInputModel topicInputModel = topicDTOModelMapper.toModel(topicInputDTO);
        Long serviceResponse = topicService.create(topicInputModel);
        return ResponseEntity
                .ok()
                .body(serviceResponse);
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody TopicInputDTO topicInputDTO) {
        TopicInputModel topicInputModel = topicDTOModelMapper.toModel(topicInputDTO);
        OperationStatus serviceResponse = topicService.update(topicInputModel);
        return ResponseEntity
                .status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }

    @DeleteMapping
    public ResponseEntity<OperationStatus> delete(
            @RequestParam("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        OperationStatus serviceResponse = topicService.delete(id);
        return ResponseEntity
                .status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }
}
