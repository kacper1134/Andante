package org.andante.speakers.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.speakers.controller.mappers.SpeakersVariantDTOModelMapper;
import org.andante.speakers.dto.SpeakersVariantInputDTO;
import org.andante.speakers.dto.SpeakersVariantOutputDTO;
import org.andante.speakers.event.SpeakersVariantEvent;
import org.andante.speakers.kafka.producer.KafkaSpeakersProducer;
import org.andante.speakers.logic.model.SpeakersVariantInput;
import org.andante.speakers.logic.model.SpeakersVariantOutput;
import org.andante.speakers.logic.service.SpeakersVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/product/speakers/variant")
@Validated
public class SpeakersVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";

    private final SpeakersVariantService speakersVariantService;
    private final SpeakersVariantDTOModelMapper speakersVariantDTOModelMapper;
    private final KafkaSpeakersProducer kafkaSpeakersProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<SpeakersVariantOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                                    @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                    @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<SpeakersVariantOutput> serviceResponse = speakersVariantService.getAllById(identifiers);
        Set<SpeakersVariantOutputDTO> speakersVariants = serviceResponse.stream()
                .map(speakersVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(speakersVariants);
    }

    @GetMapping("/bulk/{speakersId}")
    public ResponseEntity<Set<SpeakersVariantOutputDTO>> getAllBySpeakersId(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("speakersId")
                                                                            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long speakersId) {
        Set<SpeakersVariantOutput> serviceResponse = speakersVariantService.getAllBySpeakersId(speakersId);
        Set<SpeakersVariantOutputDTO> speakersVariants = serviceResponse.stream()
                .map(speakersVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(speakersVariants);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody SpeakersVariantInputDTO speakersVariantDTO) {
        SpeakersVariantInput speakersVariant = speakersVariantDTOModelMapper.toModel(speakersVariantDTO);
        SpeakersVariantOutput serviceResponse = speakersVariantService.create(speakersVariant);
        SpeakersVariantOutputDTO variantDTO = speakersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaSpeakersProducer.publish(SpeakersVariantEvent.builder()
                .speakersVariant(variantDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody SpeakersVariantInputDTO speakersVariantDTO) {
        SpeakersVariantInput speakersVariant = speakersVariantDTOModelMapper.toModel(speakersVariantDTO);
        SpeakersVariantOutput serviceResponse = speakersVariantService.modify(speakersVariant);
        SpeakersVariantOutputDTO variantDTO = speakersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaSpeakersProducer.publish(SpeakersVariantEvent.builder()
                .speakersVariant(variantDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        SpeakersVariantOutput serviceResponse = speakersVariantService.delete(identifier);
        SpeakersVariantOutputDTO variantDTO = speakersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaSpeakersProducer.publish(SpeakersVariantEvent.builder()
                .speakersVariant(variantDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
