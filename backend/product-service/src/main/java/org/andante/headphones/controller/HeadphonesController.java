package org.andante.headphones.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.headphones.controller.mapper.HeadphonesDTOModelMapper;
import org.andante.headphones.dto.HeadphonesInputDTO;
import org.andante.headphones.dto.HeadphonesOutputDTO;
import org.andante.headphones.event.HeadphonesEvent;
import org.andante.headphones.kafka.producer.KafkaHeadphonesProducer;
import org.andante.headphones.logic.model.HeadphonesInput;
import org.andante.headphones.logic.model.HeadphonesOutput;
import org.andante.headphones.logic.service.HeadphonesService;
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
@RequestMapping("/product/headphones")
@Validated
public class HeadphonesController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Headphones identifier must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";

    private final HeadphonesService headphonesService;
    private final HeadphonesDTOModelMapper headphonesDTOModelMapper;
    private final KafkaHeadphonesProducer kafkaHeadphonesProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<HeadphonesOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                               @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                               @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<HeadphonesOutput> serviceResponse = headphonesService.findAllById(identifiers);
        Set<HeadphonesOutputDTO> headphones = serviceResponse.stream()
                .map(headphonesDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(headphones);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<HeadphonesOutputDTO>> getByQuery(@Valid ProductQuerySpecification query, @RequestParam("rating")
    @NotNull(message = RATING_NULL_ERROR_MESSAGE) @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {

        Page<HeadphonesOutput> serviceResponse = headphonesService.getByQuery(query, minimumRating);

        Page<HeadphonesOutputDTO> matchingHeadphones = serviceResponse.map(headphonesDTOModelMapper::toDTO);

        return ResponseEntity.ok()
                .body(matchingHeadphones);
    }


    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody HeadphonesInputDTO headphonesInputDTO) {
        HeadphonesInput headphonesInput = headphonesDTOModelMapper.toModel(headphonesInputDTO);
        HeadphonesOutput serviceResponse = headphonesService.create(headphonesInput);
        HeadphonesOutputDTO headphonesDTO = headphonesDTOModelMapper.toDTO(serviceResponse);

        kafkaHeadphonesProducer.publish(HeadphonesEvent.builder()
                .headphones(headphonesDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok(serviceResponse.getId());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody HeadphonesInputDTO headphonesInputDTO) {
        HeadphonesInput headphonesInput = headphonesDTOModelMapper.toModel(headphonesInputDTO);
        HeadphonesOutput serviceResponse = headphonesService.modify(headphonesInput);
        HeadphonesOutputDTO headphonesDTO = headphonesDTOModelMapper.toDTO(serviceResponse);

        kafkaHeadphonesProducer.publish(HeadphonesEvent.builder()
                .headphones(headphonesDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        HeadphonesOutput serviceResponse = headphonesService.delete(identifier);
        HeadphonesOutputDTO headphonesDTO = headphonesDTOModelMapper.toDTO(serviceResponse);

        kafkaHeadphonesProducer.publish(HeadphonesEvent.builder()
                .headphones(headphonesDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
