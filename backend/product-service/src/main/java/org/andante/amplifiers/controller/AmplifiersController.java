package org.andante.amplifiers.controller;

import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.controller.mapper.AmplifiersDTOModelMapper;
import org.andante.amplifiers.dto.AmplifiersInputDTO;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.amplifiers.event.AmplifierEvent;
import org.andante.amplifiers.kafka.producer.KafkaAmplifierProducer;
import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.amplifiers.logic.service.AmplifiersService;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/product/amplifier")
@Validated
public class AmplifiersController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Amplifiers identifier must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";

    private final AmplifiersService amplifiersService;
    private final AmplifiersDTOModelMapper amplifiersDTOModelMapper;
    private final KafkaAmplifierProducer kafkaAmplifierProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<AmplifiersOutputDTO>> getAllById(@RequestParam("ids") @Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE)
                                                               @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                               @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<AmplifiersOutput> serviceResponse = amplifiersService.getAllById(identifiers);
        Set<AmplifiersOutputDTO> amplifiersFound = serviceResponse.stream()
                .map(amplifiersDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(amplifiersFound);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<AmplifiersOutputDTO>> getByQuery(@Valid ProductQuerySpecification query, @RequestParam("rating")
                                                                @NotNull(message = RATING_NULL_ERROR_MESSAGE) @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {

        Page<AmplifiersOutput> serviceResponse = amplifiersService.getByQuery(query, minimumRating);

        Page<AmplifiersOutputDTO> matchingAmplifiers = serviceResponse.map(amplifiersDTOModelMapper::toDTO);

        return ResponseEntity.ok()
                .body(matchingAmplifiers);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid AmplifiersInputDTO amplifiersToCreate) {
        AmplifiersInput model = amplifiersDTOModelMapper.toModel(amplifiersToCreate);
        AmplifiersOutput serviceResponse = amplifiersService.create(model);
        AmplifiersOutputDTO amplifiersDTO = amplifiersDTOModelMapper.toDTO(serviceResponse);

        kafkaAmplifierProducer.publish(AmplifierEvent.builder()
                .amplifiers(amplifiersDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@RequestBody @Valid AmplifiersInputDTO amplifiersToModify) {
        AmplifiersInput model = amplifiersDTOModelMapper.toModel(amplifiersToModify);
        AmplifiersOutput serviceResponse = amplifiersService.update(model);
        AmplifiersOutputDTO amplifiersDTO = amplifiersDTOModelMapper.toDTO(serviceResponse);

        kafkaAmplifierProducer.publish(AmplifierEvent.builder()
                .amplifiers(amplifiersDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@PathVariable("id") @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        AmplifiersOutput serviceResponse = amplifiersService.delete(identifier);
        AmplifiersOutputDTO amplifiersDTO = amplifiersDTOModelMapper.toDTO(serviceResponse);

        kafkaAmplifierProducer.publish(AmplifierEvent.builder()
                .amplifiers(amplifiersDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
