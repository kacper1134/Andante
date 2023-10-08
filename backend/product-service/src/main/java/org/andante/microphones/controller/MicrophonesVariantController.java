package org.andante.microphones.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.microphones.controller.mapper.MicrophonesVariantDTOModelMapper;
import org.andante.microphones.dto.MicrophonesVariantInputDTO;
import org.andante.microphones.dto.MicrophonesVariantOutputDTO;
import org.andante.microphones.event.MicrophoneVariantEvent;
import org.andante.microphones.kafka.producer.KafkaMicrophoneProducer;
import org.andante.microphones.logic.model.MicrophonesVariantInput;
import org.andante.microphones.logic.model.MicrophonesVariantOutput;
import org.andante.microphones.logic.service.MicrophonesVariantService;
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
@RequestMapping("/product/microphone/variant")
@Validated
public class MicrophonesVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";

    private final MicrophonesVariantService microphonesVariantService;
    private final MicrophonesVariantDTOModelMapper microphonesVariantDTOModelMapper;
    private final KafkaMicrophoneProducer kafkaMicrophoneProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<MicrophonesVariantOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                                       @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                       @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<MicrophonesVariantOutput> serviceResponse = microphonesVariantService.getAllById(identifiers);
        Set<MicrophonesVariantOutputDTO> microphonesVariants = serviceResponse.stream()
                .map(microphonesVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(microphonesVariants);
    }

    @GetMapping("/bulk/{microphoneId}")
    public ResponseEntity<Set<MicrophonesVariantOutputDTO>> getAllByMicrophoneId(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("microphoneId")
                                                                                 @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long microphoneIdentifier) {
        Set<MicrophonesVariantOutput> serviceResponse = microphonesVariantService.getAllByMicrophoneId(microphoneIdentifier);
        Set<MicrophonesVariantOutputDTO> microphonesVariants = serviceResponse.stream()
                .map(microphonesVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(microphonesVariants);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody MicrophonesVariantInputDTO microphonesVariantDTO) {
        MicrophonesVariantInput microphonesVariant = microphonesVariantDTOModelMapper.toModel(microphonesVariantDTO);
        MicrophonesVariantOutput serviceResponse = microphonesVariantService.create(microphonesVariant);
        MicrophonesVariantOutputDTO variantDTO = microphonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaMicrophoneProducer.publish(MicrophoneVariantEvent.builder()
                .microphoneVariant(variantDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody MicrophonesVariantInputDTO microphonesVariantDTO) {
        MicrophonesVariantInput microphonesVariant = microphonesVariantDTOModelMapper.toModel(microphonesVariantDTO);
        MicrophonesVariantOutput serviceResponse = microphonesVariantService.modify(microphonesVariant);
        MicrophonesVariantOutputDTO variantDTO = microphonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaMicrophoneProducer.publish(MicrophoneVariantEvent.builder()
                .microphoneVariant(variantDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        MicrophonesVariantOutput serviceResponse = microphonesVariantService.delete(identifier);
        MicrophonesVariantOutputDTO variantDTO = microphonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaMicrophoneProducer.publish(MicrophoneVariantEvent.builder()
                .microphoneVariant(variantDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
