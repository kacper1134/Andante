package org.andante.subwoofers.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.subwoofers.controller.mapper.SubwoofersDTOModelMapper;
import org.andante.subwoofers.dto.SubwoofersInputDTO;
import org.andante.subwoofers.dto.SubwoofersOutputDTO;
import org.andante.subwoofers.event.SubwoofersEvent;
import org.andante.subwoofers.kafka.producer.KafkaSubwoofersProducer;
import org.andante.subwoofers.logic.model.SubwoofersInput;
import org.andante.subwoofers.logic.model.SubwoofersOutput;
import org.andante.subwoofers.logic.service.SubwoofersService;
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
@RequestMapping("/product/subwoofer")
@Validated
public class SubwoofersController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Provided identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Subwoofers identifier must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";

    private final SubwoofersService subwoofersService;
    private final SubwoofersDTOModelMapper subwoofersDTOModelMapper;
    private final KafkaSubwoofersProducer kafkaSubwoofersProducer;

    @GetMapping("/bulk")
    public ResponseEntity<Set<SubwoofersOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE) @RequestParam("ids")
                                                               @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                               @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<SubwoofersOutput> serviceResponse = subwoofersService.getAllById(identifiers);
        Set<SubwoofersOutputDTO> subwoofers = serviceResponse.stream()
                .map(subwoofersDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(subwoofers);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody SubwoofersInputDTO subwoofersInputDTO) {
        SubwoofersInput subwoofersInput = subwoofersDTOModelMapper.toModel(subwoofersInputDTO);
        SubwoofersOutput serviceResponse = subwoofersService.create(subwoofersInput);
        SubwoofersOutputDTO subwoofersDTO = subwoofersDTOModelMapper.toDTO(serviceResponse);

        kafkaSubwoofersProducer.publish(SubwoofersEvent.builder()
                .subwoofers(subwoofersDTO)
                .operationType(OperationType.CREATE)
                .build());


        return ResponseEntity.ok(serviceResponse.getId());
    }

    @GetMapping("/query")
    public ResponseEntity<Page<SubwoofersOutputDTO>> getByQuery(@Valid ProductQuerySpecification query, @RequestParam("rating")
    @NotNull(message = RATING_NULL_ERROR_MESSAGE) @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {

        Page<SubwoofersOutput> serviceResponse = subwoofersService.getByQuery(query, minimumRating);

        Page<SubwoofersOutputDTO> matchingSubwoofers = serviceResponse.map(subwoofersDTOModelMapper::toDTO);

        return ResponseEntity.ok()
                .body(matchingSubwoofers);
    }


    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody SubwoofersInputDTO subwoofersInputDTO) {
        SubwoofersInput subwoofersInput = subwoofersDTOModelMapper.toModel(subwoofersInputDTO);
        SubwoofersOutput serviceResponse = subwoofersService.modify(subwoofersInput);
        SubwoofersOutputDTO subwoofersDTO = subwoofersDTOModelMapper.toDTO(serviceResponse);

        kafkaSubwoofersProducer.publish(SubwoofersEvent.builder()
                .subwoofers(subwoofersDTO)
                .operationType(OperationType.MODIFY)
                .build());


        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        SubwoofersOutput serviceResponse = subwoofersService.delete(identifier);
        SubwoofersOutputDTO subwoofersDTO = subwoofersDTOModelMapper.toDTO(serviceResponse);

        kafkaSubwoofersProducer.publish(SubwoofersEvent.builder()
                .subwoofers(subwoofersDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
