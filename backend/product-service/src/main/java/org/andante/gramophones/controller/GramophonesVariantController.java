package org.andante.gramophones.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.gramophones.controller.mapper.GramophonesVariantDTOModelMapper;
import org.andante.gramophones.dto.GramophonesVariantInputDTO;
import org.andante.gramophones.dto.GramophonesVariantOutputDTO;
import org.andante.gramophones.event.GramophoneVariantEvent;
import org.andante.gramophones.kafka.producer.KafkaGramophoneProducer;
import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;
import org.andante.gramophones.logic.service.GramophonesVariantService;
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
@RequestMapping("/product/gramophone/variant")
@Validated
public class GramophonesVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";

    private final GramophonesVariantService gramophonesVariantService;
    private final GramophonesVariantDTOModelMapper gramophonesVariantDTOModelMapper;
    private final KafkaGramophoneProducer kafkaGramophoneProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<GramophonesVariantOutputDTO>> getAllById(@RequestParam("ids") @Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE)
                                                                       @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                       @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<GramophonesVariantOutput> serviceResponse = gramophonesVariantService.getAllByIds(identifiers);
        Set<GramophonesVariantOutputDTO> gramophonesVariants = serviceResponse.stream()
                .map(gramophonesVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(gramophonesVariants);
    }

    @GetMapping("/bulk/{productId}")
    public ResponseEntity<Set<GramophonesVariantOutputDTO>> getAllByGramophoneId(@PathVariable("productId") @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                                 @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long gramophonesId) {
        Set<GramophonesVariantOutput> serviceResponse = gramophonesVariantService.getAllByGramophoneId(gramophonesId);
        Set<GramophonesVariantOutputDTO> gramophoneVariants = serviceResponse.stream()
                .map(gramophonesVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(gramophoneVariants);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody GramophonesVariantInputDTO gramophonesVariantDTO) {
        GramophonesVariantInput gramophonesVariant = gramophonesVariantDTOModelMapper.toModel(gramophonesVariantDTO);
        GramophonesVariantOutput serviceResponse = gramophonesVariantService.create(gramophonesVariant);
        GramophonesVariantOutputDTO variantDTO = gramophonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaGramophoneProducer.publish(GramophoneVariantEvent.builder()
                .gramophoneVariant(variantDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody GramophonesVariantInputDTO gramophonesVariantDTO) {
        GramophonesVariantInput gramophonesVariant = gramophonesVariantDTOModelMapper.toModel(gramophonesVariantDTO);
        GramophonesVariantOutput serviceResponse = gramophonesVariantService.modify(gramophonesVariant);
        GramophonesVariantOutputDTO variantDTO = gramophonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaGramophoneProducer.publish(GramophoneVariantEvent.builder()
                .gramophoneVariant(variantDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        GramophonesVariantOutput serviceResponse = gramophonesVariantService.delete(identifier);
        GramophonesVariantOutputDTO variantDTO = gramophonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaGramophoneProducer.publish(GramophoneVariantEvent.builder()
                .gramophoneVariant(variantDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
