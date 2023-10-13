package org.andante.headphones.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.headphones.controller.mapper.HeadphonesVariantDTOModelMapper;
import org.andante.headphones.dto.HeadphonesVariantInputDTO;
import org.andante.headphones.dto.HeadphonesVariantOutputDTO;
import org.andante.headphones.event.HeadphonesVariantEvent;
import org.andante.headphones.kafka.producer.KafkaHeadphonesProducer;
import org.andante.headphones.logic.model.HeadphonesVariantInput;
import org.andante.headphones.logic.model.HeadphonesVariantOutput;
import org.andante.headphones.logic.service.HeadphonesVariantService;
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
@RequestMapping("/product/headphones/variant")
@Validated
public class HeadphonesVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";

    private final HeadphonesVariantService headphonesVariantService;
    private final HeadphonesVariantDTOModelMapper headphonesVariantDTOModelMapper;
    private final KafkaHeadphonesProducer kafkaHeadphonesProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<HeadphonesVariantOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                                      @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                      @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<HeadphonesVariantOutput> serviceResponse = headphonesVariantService.getAllById(identifiers);
        Set<HeadphonesVariantOutputDTO> headphonesVariants = serviceResponse.stream()
                .map(headphonesVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(headphonesVariants);
    }

    @GetMapping("/bulk/{headphonesId}")
    public ResponseEntity<Set<HeadphonesVariantOutputDTO>> getAllByHeadphonesId(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("headphonesId")
                                                                                @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long headphonesId) {
        Set<HeadphonesVariantOutput> serviceResponse = headphonesVariantService.getAllByHeadphonesId(headphonesId);
        Set<HeadphonesVariantOutputDTO> headphonesVariants = serviceResponse.stream()
                .map(headphonesVariantDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(headphonesVariants);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody HeadphonesVariantInputDTO headphonesVariantDTO) {
        HeadphonesVariantInput headphonesVariant = headphonesVariantDTOModelMapper.toModel(headphonesVariantDTO);
        HeadphonesVariantOutput serviceResponse = headphonesVariantService.create(headphonesVariant);
        HeadphonesVariantOutputDTO variantDTO = headphonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaHeadphonesProducer.publish(HeadphonesVariantEvent.builder()
                .headphonesVariant(variantDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody HeadphonesVariantInputDTO headphonesVariantDTO) {
        HeadphonesVariantInput headphonesVariant = headphonesVariantDTOModelMapper.toModel(headphonesVariantDTO);
        HeadphonesVariantOutput serviceResponse = headphonesVariantService.modify(headphonesVariant);
        HeadphonesVariantOutputDTO variantDTO = headphonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaHeadphonesProducer.publish(HeadphonesVariantEvent.builder()
                .headphonesVariant(variantDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        HeadphonesVariantOutput serviceResponse = headphonesVariantService.delete(identifier);
        HeadphonesVariantOutputDTO variantDTO = headphonesVariantDTOModelMapper.toDTO(serviceResponse);

        kafkaHeadphonesProducer.publish(HeadphonesVariantEvent.builder()
                .headphonesVariant(variantDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
