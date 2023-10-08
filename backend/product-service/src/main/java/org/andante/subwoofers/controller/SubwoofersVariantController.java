package org.andante.subwoofers.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.subwoofers.controller.mapper.SubwoofersVariantDTOModelMapper;
import org.andante.subwoofers.dto.SubwoofersVariantInputDTO;
import org.andante.subwoofers.dto.SubwoofersVariantOutputDTO;
import org.andante.subwoofers.event.SubwoofersVariantEvent;
import org.andante.subwoofers.kafka.producer.KafkaSubwoofersProducer;
import org.andante.subwoofers.logic.model.SubwoofersVariantInput;
import org.andante.subwoofers.logic.model.SubwoofersVariantOutput;
import org.andante.subwoofers.logic.service.SubwoofersVariantService;
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
@RequestMapping("/product/subwoofer/variant")
@Validated
public class SubwoofersVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";

    private final SubwoofersVariantService subwoofersVariantService;
    private final SubwoofersVariantDTOModelMapper subwoofersVariantDTOModelMapper;
    private final KafkaSubwoofersProducer kafkaSubwoofersProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<SubwoofersVariantOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                                      @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                      @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<SubwoofersVariantOutput> serviceResponse = subwoofersVariantService.getAllById(identifiers);
        Set<SubwoofersVariantOutputDTO> subwoofersVariants = serviceResponse.stream()
                .map(subwoofersVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(subwoofersVariants);
    }

    @GetMapping("/bulk/{subwooferId}")
    public ResponseEntity<Set<SubwoofersVariantOutputDTO>> getAllBySubwooferId(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("subwooferId")
                                                                               @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long subwooferId) {
        Set<SubwoofersVariantOutput> serviceResponse = subwoofersVariantService.getAllBySubwooferId(subwooferId);
        Set<SubwoofersVariantOutputDTO> subwoofersVariants = serviceResponse.stream()
                .map(subwoofersVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(subwoofersVariants);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody SubwoofersVariantInputDTO subwoofersVariantDTO) {
        SubwoofersVariantInput subwoofersVariant = subwoofersVariantDTOModelMapper.toModel(subwoofersVariantDTO);
        SubwoofersVariantOutput serviceResponse = subwoofersVariantService.create(subwoofersVariant);
        SubwoofersVariantOutputDTO variantDTO = subwoofersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaSubwoofersProducer.publish(SubwoofersVariantEvent.builder()
                .subwoofersVariant(variantDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody SubwoofersVariantInputDTO subwoofersVariantDTO) {
        SubwoofersVariantInput subwoofersVariant = subwoofersVariantDTOModelMapper.toModel(subwoofersVariantDTO);
        SubwoofersVariantOutput serviceResponse = subwoofersVariantService.modify(subwoofersVariant);
        SubwoofersVariantOutputDTO variantDTO = subwoofersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaSubwoofersProducer.publish(SubwoofersVariantEvent.builder()
                .subwoofersVariant(variantDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        SubwoofersVariantOutput serviceResponse = subwoofersVariantService.delete(identifier);
        SubwoofersVariantOutputDTO variantDTO = subwoofersVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaSubwoofersProducer.publish(SubwoofersVariantEvent.builder()
                .subwoofersVariant(variantDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
