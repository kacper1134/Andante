package org.andante.microphones.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.microphones.controller.mapper.MicrophonesDTOModelMapper;
import org.andante.microphones.dto.MicrophonesInputDTO;
import org.andante.microphones.dto.MicrophonesOutputDTO;
import org.andante.microphones.event.MicrophoneEvent;
import org.andante.microphones.kafka.producer.KafkaMicrophoneProducer;
import org.andante.microphones.logic.model.MicrophonesInput;
import org.andante.microphones.logic.model.MicrophonesOutput;
import org.andante.microphones.logic.service.MicrophonesService;
import org.andante.product.dto.ProductQuerySpecification;
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
@RequestMapping("/product/microphone")
@Validated
public class MicrophonesController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Microphone identifier must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";

    private final MicrophonesService microphonesService;
    private final MicrophonesDTOModelMapper microphonesDTOModelMapper;
    private final KafkaMicrophoneProducer kafkaMicrophoneProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<MicrophonesOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                                @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<MicrophonesOutput> serviceResponse = microphonesService.getAllByIds(identifiers);
        Set<MicrophonesOutputDTO> microphones = serviceResponse.stream()
                .map(microphonesDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(microphones);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<MicrophonesOutputDTO>> getByQuery(@Valid ProductQuerySpecification query, @RequestParam("rating")
    @NotNull(message = RATING_NULL_ERROR_MESSAGE) @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {

        Page<MicrophonesOutput> serviceResponse = microphonesService.getByQuery(query, minimumRating);

        Page<MicrophonesOutputDTO> matchingMicrophones = serviceResponse.map(microphonesDTOModelMapper::toDTO);

        return ResponseEntity.ok()
                .body(matchingMicrophones);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody MicrophonesInputDTO microphonesInputDTO) {
        MicrophonesInput microphonesInput = microphonesDTOModelMapper.toModel(microphonesInputDTO);
        MicrophonesOutput serviceResponse = microphonesService.create(microphonesInput);
        MicrophonesOutputDTO microphonesDTO = microphonesDTOModelMapper.toDTO(serviceResponse);

        kafkaMicrophoneProducer.publish(MicrophoneEvent.builder()
                .microphone(microphonesDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody MicrophonesInputDTO microphonesInputDTO) {
        MicrophonesInput microphonesInput = microphonesDTOModelMapper.toModel(microphonesInputDTO);
        MicrophonesOutput serviceResponse = microphonesService.modify(microphonesInput);
        MicrophonesOutputDTO microphonesDTO = microphonesDTOModelMapper.toDTO(serviceResponse);

        kafkaMicrophoneProducer.publish(MicrophoneEvent.builder()
                .microphone(microphonesDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        MicrophonesOutput serviceResponse = microphonesService.delete(identifier);
        MicrophonesOutputDTO microphonesDTO = microphonesDTOModelMapper.toDTO(serviceResponse);

        kafkaMicrophoneProducer.publish(MicrophoneEvent.builder()
                .microphone(microphonesDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
