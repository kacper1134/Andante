package org.andante.amplifiers.controller;

import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.controller.mapper.AmplifiersVariantDTOModelMapper;
import org.andante.amplifiers.dto.AmplifiersVariantInputDTO;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.amplifiers.event.AmplifierVariantEvent;
import org.andante.amplifiers.kafka.producer.KafkaAmplifierProducer;
import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
import org.andante.amplifiers.logic.service.AmplifiersVariantService;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
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
@RequestMapping("/product/amplifier/variant")
@Validated
public class AmplifiersVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";

    private final AmplifiersVariantService amplifiersVariantService;
    private final AmplifiersVariantDTOModelMapper amplifiersVariantDTOModelMapper;
    private final KafkaAmplifierProducer kafkaAmplifierProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<AmplifiersVariantOutputDTO>> getAllById(@RequestParam("ids") @Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE)
                                                                      @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                      @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<AmplifiersVariantOutput> serviceResponse = amplifiersVariantService.getAllById(identifiers);
        Set<AmplifiersVariantOutputDTO> amplifiersVariants = serviceResponse.stream()
                .map(amplifiersVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(amplifiersVariants);
    }

    @GetMapping("/bulk/{productId}")
    public ResponseEntity<Set<AmplifiersVariantOutputDTO>> getAllByAmplifierId(@PathVariable("productId") @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                               @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long productId) {
        Set<AmplifiersVariantOutput> serviceResponse = amplifiersVariantService.getAllByProductId(productId);
        Set<AmplifiersVariantOutputDTO> amplifierVariants = serviceResponse.stream()
                .map(amplifiersVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(amplifierVariants);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid AmplifiersVariantInputDTO amplifiersVariantDTO) {
        AmplifiersVariantInput variantToCreate = amplifiersVariantDTOModelMapper.toModel(amplifiersVariantDTO);
        AmplifiersVariantOutput serviceResponse = amplifiersVariantService.create(variantToCreate);
        AmplifiersVariantOutputDTO variantDTO = amplifiersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaAmplifierProducer.publish(AmplifierVariantEvent.builder()
                .amplifierVariant(variantDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> update(@RequestBody @Valid AmplifiersVariantInputDTO amplifiersVariantDTO) {
        AmplifiersVariantInput variantToModify = amplifiersVariantDTOModelMapper.toModel(amplifiersVariantDTO);
        AmplifiersVariantOutput serviceResponse = amplifiersVariantService.update(variantToModify);
        AmplifiersVariantOutputDTO variantDTO = amplifiersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaAmplifierProducer.publish(AmplifierVariantEvent.builder()
                .amplifierVariant(variantDTO)
                .operationType(OperationType.MODIFY)
                .build());


        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@PathVariable("id") @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        AmplifiersVariantOutput serviceResponse = amplifiersVariantService.delete(identifier);
        AmplifiersVariantOutputDTO variantDTO = amplifiersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaAmplifierProducer.publish(AmplifierVariantEvent.builder()
                .amplifierVariant(variantDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
