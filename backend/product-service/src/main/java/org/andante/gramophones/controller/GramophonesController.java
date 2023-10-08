package org.andante.gramophones.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.gramophones.controller.mapper.GramophonesDTOModelMapper;
import org.andante.gramophones.dto.GramophonesInputDTO;
import org.andante.gramophones.dto.GramophonesOutputDTO;
import org.andante.gramophones.event.GramophoneEvent;
import org.andante.gramophones.kafka.producer.KafkaGramophoneProducer;
import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesOutput;
import org.andante.gramophones.logic.service.GramophonesService;
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
@RequestMapping("/product/gramophones")
@Validated
public class GramophonesController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Gramophones identifier must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";

    private final GramophonesService gramophonesService;
    private final GramophonesDTOModelMapper gramophonesDTOModelMapper;
    private final KafkaGramophoneProducer kafkaGramophoneProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<GramophonesOutputDTO>> getAllById(@RequestParam("ids") @Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE)
                                                                @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<GramophonesOutput> serviceResponse = gramophonesService.getAllById((identifiers));
        Set<GramophonesOutputDTO> gramophones = serviceResponse.stream()
                .map(gramophonesDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(gramophones);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<GramophonesOutputDTO>> getByQuery(@Valid ProductQuerySpecification query, @RequestParam("rating")
    @NotNull(message = RATING_NULL_ERROR_MESSAGE) @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {

        Page<GramophonesOutput> serviceResponse = gramophonesService.getByQuery(query, minimumRating);

        Page<GramophonesOutputDTO> matchingGramophones = serviceResponse.map(gramophonesDTOModelMapper::toDTO);

        return ResponseEntity.ok()
                .body(matchingGramophones);
    }


    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody GramophonesInputDTO gramophonesInput) {
        GramophonesInput gramophonesToCreate = gramophonesDTOModelMapper.toModel(gramophonesInput);
        GramophonesOutput serviceResponse = gramophonesService.create(gramophonesToCreate);
        GramophonesOutputDTO gramophonesDTO = gramophonesDTOModelMapper.toDTO(serviceResponse);


        kafkaGramophoneProducer.publish(GramophoneEvent.builder()
                .gramophone(gramophonesDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody GramophonesInputDTO gramophonesInput) {
        GramophonesInput gramophonesToModify = gramophonesDTOModelMapper.toModel(gramophonesInput);
        GramophonesOutput serviceResponse = gramophonesService.modify(gramophonesToModify);
        GramophonesOutputDTO gramophonesDTO = gramophonesDTOModelMapper.toDTO(serviceResponse);

        kafkaGramophoneProducer.publish(GramophoneEvent.builder()
                .gramophone(gramophonesDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        GramophonesOutput serviceResponse = gramophonesService.delete(identifier);
        GramophonesOutputDTO gramophoneDTO = gramophonesDTOModelMapper.toDTO(serviceResponse);

        kafkaGramophoneProducer.publish(GramophoneEvent.builder()
                .gramophone(gramophoneDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
