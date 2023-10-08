package org.andante.product.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.product.controller.mapper.ProducerDTOModelMapper;
import org.andante.product.dto.ProducerDTO;
import org.andante.product.dto.ProductOutputDTO;
import org.andante.product.event.ProducerEvent;
import org.andante.product.kafka.producer.KafkaProductProducer;
import org.andante.product.logic.model.Producer;
import org.andante.product.logic.model.ProductOutput;
import org.andante.product.logic.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/product/producer")
@Validated
public class ProducerController {

    private static final String NAMES_LIST_ERROR_MESSAGE = "List of provided producer's names must contain at least {min} element(s)";
    private static final String NAMES_LIST_NULL_ERROR_MESSAGE = "List of provided producer's names must not be a null";
    private static final String NAME_ERROR_MESSAGE = "Producer name '${validatedValue}' must not be blank";
    private static final String PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE = "Page number '${validatedValue}' must not be a negative number";
    private static final String PAGE_NUMBER_NULL_ERROR_MESSAGE = "Page number must not be null";
    private static final String PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE = "Page size '${validatedValue}' must be a positive number";
    private static final String PAGE_SIZE_NULL_ERROR_MESSAGE = "Page size must not be a null";

    private final ProducerService producerService;
    private final ProducerDTOModelMapper producerDTOModelMapper;
    private final KafkaProductProducer kafkaProductProducer;

    @GetMapping("/all")
    public ResponseEntity<Set<ProducerDTO>> getAll(@RequestParam("names") @Size(min = 1, message = NAMES_LIST_ERROR_MESSAGE)
                                                   @NotNull(message = NAMES_LIST_NULL_ERROR_MESSAGE) Set<@NotBlank(message = NAME_ERROR_MESSAGE) String> names) {
        Set<Producer> serviceResponse = producerService.getAllById(names);
        Set<ProducerDTO> producersFound = serviceResponse.stream()
                .map(producerDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(producersFound);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Set<ProductOutputDTO>> getProducts(@PathVariable("name") @NotBlank(message = NAME_ERROR_MESSAGE) String name) {
        Set<ProductOutput> serviceResponse = producerService.getAllProducts(name);
        Set<ProductOutputDTO> productsFound = serviceResponse.stream()
                .map(ProductOutput::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(productsFound);
    }

    @GetMapping("/top")
    public ResponseEntity<List<ProducerDTO>> getTopProducers(@PositiveOrZero(message = PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE)
                                                             @NotNull(message = PAGE_NUMBER_NULL_ERROR_MESSAGE) @RequestParam(name = "page") Integer page,
                                                             @Positive(message = PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE)
                                                             @NotNull(message = PAGE_SIZE_NULL_ERROR_MESSAGE) @RequestParam(name = "size") Integer pageSize) {
        List<Producer> serviceResponse = producerService.getBiggestProducers(page, pageSize);
        List<ProducerDTO> topProducers = serviceResponse.stream()
                .map(producerDTOModelMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topProducers);
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody ProducerDTO producerDTO) {
        Producer producerToCreate = producerDTOModelMapper.toModel(producerDTO);
        Producer serviceResponse = producerService.create(producerToCreate);
        ProducerDTO mappedProducer = producerDTOModelMapper.toDTO(serviceResponse);

        kafkaProductProducer.publish(ProducerEvent.builder()
                .producer(mappedProducer)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getName());
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody ProducerDTO producerDTO) {
        Producer producerToModify = producerDTOModelMapper.toModel(producerDTO);
        Producer serviceResponse = producerService.modify(producerToModify);
        ProducerDTO mappedProducer = producerDTOModelMapper.toDTO(serviceResponse);

        kafkaProductProducer.publish(ProducerEvent.builder()
                .producer(mappedProducer)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<OperationStatus> delete(@PathVariable("name") @NotBlank(message = NAME_ERROR_MESSAGE) String name) {
        Producer serviceResponse = producerService.delete(name);
        ProducerDTO producerDTO = producerDTOModelMapper.toDTO(serviceResponse);

        kafkaProductProducer.publish(ProducerEvent.builder()
                .producer(producerDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }
}
