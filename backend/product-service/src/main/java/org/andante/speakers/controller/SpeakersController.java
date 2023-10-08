package org.andante.speakers.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.speakers.controller.mappers.SpeakersDTOModelMapper;
import org.andante.speakers.dto.SpeakersInputDTO;
import org.andante.speakers.dto.SpeakersOutputDTO;
import org.andante.speakers.event.SpeakersEvent;
import org.andante.speakers.kafka.producer.KafkaSpeakersProducer;
import org.andante.speakers.logic.model.SpeakersInput;
import org.andante.speakers.logic.model.SpeakersOutput;
import org.andante.speakers.logic.service.SpeakersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/product/speakers")
@Validated
public class SpeakersController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Speakers identifier must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";

    private final SpeakersService speakersService;
    private final SpeakersDTOModelMapper speakersDTOModelMapper;
    private final KafkaSpeakersProducer kafkaSpeakersProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<SpeakersOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                             @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                             @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<SpeakersOutput> serviceResponse = speakersService.getAllById(identifiers);
        Set<SpeakersOutputDTO> speakers = serviceResponse.stream()
                .map(speakersDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(speakers);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<SpeakersOutputDTO>> getByQuery(@Valid ProductQuerySpecification query, @RequestParam("rating")
    @NotNull(message = RATING_NULL_ERROR_MESSAGE) @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {

        Page<SpeakersOutput> serviceResponse = speakersService.getByQuery(query, minimumRating);

        Page<SpeakersOutputDTO> matchingSpeakers = serviceResponse.map(speakersDTOModelMapper::toDTO);

        return ResponseEntity.ok()
                .body(matchingSpeakers);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody SpeakersInputDTO speakersInputDTO) {
        SpeakersInput speakersInput = speakersDTOModelMapper.toModel(speakersInputDTO);
        SpeakersOutput serviceResponse = speakersService.create(speakersInput);
        SpeakersOutputDTO speakersDTO = speakersDTOModelMapper.toDTO(serviceResponse);

        kafkaSpeakersProducer.publish(SpeakersEvent.builder()
                .speakers(speakersDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody SpeakersInputDTO speakersInputDTO) {
        SpeakersInput speakersInput = speakersDTOModelMapper.toModel(speakersInputDTO);
        SpeakersOutput serviceResponse = speakersService.modify(speakersInput);
        SpeakersOutputDTO speakersDTO = speakersDTOModelMapper.toDTO(serviceResponse);

        kafkaSpeakersProducer.publish(SpeakersEvent.builder()
                .speakers(speakersDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        SpeakersOutput serviceResponse = speakersService.delete(identifier);
        SpeakersOutputDTO speakersDTO = speakersDTOModelMapper.toDTO(serviceResponse);

        kafkaSpeakersProducer.publish(SpeakersEvent.builder()
                .speakers(speakersDTO)
                .operationType(OperationType.DELETE)
                .build());


        return ResponseEntity.ok()
                .build();
    }
}
