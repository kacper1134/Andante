package org.andante.orders.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.orders.controller.mapper.OrderEntryDTOModelMapper;
import org.andante.orders.dto.OrderEntryInputDTO;
import org.andante.orders.dto.OrderEntryOutputDTO;
import org.andante.orders.event.OrderEntryEvent;
import org.andante.orders.kafka.producer.KafkaOrderProducer;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderEntryOutput;
import org.andante.orders.logic.service.OrderEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/order/orderEntry")
@Validated
public class OrderEntryController {

    private static final String POSITIVE_IDENTIFIER_ERROR_MESSAGE = "Order entry identifier '${validatedValue}' must be a positive value";
    private static final String NULL_IDENTIFIER_ERROR_MESSAGE = "Order entry identifier must not be null";

    private final OrderEntryService orderEntryService;
    private final OrderEntryDTOModelMapper orderEntryDTOModelMapper;
    private final KafkaOrderProducer kafkaOrderProducer;

    @GetMapping("/bulk/order/{orderId}")
    public ResponseEntity<Set<OrderEntryOutputDTO>> getAllByOrderId(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                                    @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("orderId") Long orderId) {
        Set<OrderEntryOutput> serviceResponse = orderEntryService.getAllByOrderId(orderId);
        Set<OrderEntryOutputDTO> orderEntriesFound = serviceResponse.stream()
                .map(orderEntryDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(orderEntriesFound);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderEntryOutputDTO> getById(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                       @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long id) {
        Optional<OrderEntryOutput> serviceResponse = orderEntryService.getById(id);
        Optional<OrderEntryOutputDTO> orderEntryFound = serviceResponse.map(orderEntryDTOModelMapper::toDTO);

        if (orderEntryFound.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity.ok(orderEntryFound.get());
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody OrderEntryInputDTO orderEntriesToCreate) {
        OrderEntryInput model = orderEntryDTOModelMapper.toModel(orderEntriesToCreate);
        OrderEntryOutput serviceResponse = orderEntryService.create(model);
        OrderEntryOutputDTO orderEntryOutputDTO = orderEntryDTOModelMapper.toDTO(serviceResponse);

        kafkaOrderProducer.publish(OrderEntryEvent.builder()
                .orderEntries(orderEntryOutputDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PostMapping("/bulk")
    public ResponseEntity<Set<Long>> bulkCreate(@RequestBody Set<@Valid OrderEntryInputDTO> orderEntriesToCreate)  {
        Set<OrderEntryInput> models = orderEntryDTOModelMapper.toModel(orderEntriesToCreate);
        Set<OrderEntryOutput> serviceResponses = orderEntryService.bulkCreate(models);
        Set<OrderEntryOutputDTO> orderEntryOutputDTOS = orderEntryDTOModelMapper.toDTO(serviceResponses);

        orderEntryOutputDTOS.forEach(orderEntryOutputDTO -> kafkaOrderProducer.publish(OrderEntryEvent.builder()
                .orderEntries(orderEntryOutputDTO)
                .operationType(OperationType.CREATE)
                .build()));

        return ResponseEntity.ok()
                .body(orderEntryOutputDTOS.stream()
                        .map(OrderEntryOutputDTO::getId)
                        .collect(Collectors.toSet()));
    }

    @PutMapping
    public ResponseEntity<OperationStatus> update(@Valid @RequestBody OrderEntryInputDTO orderEntriesToModify) {
        OrderEntryInput model = orderEntryDTOModelMapper.toModel(orderEntriesToModify);
        OrderEntryOutput serviceResponse = orderEntryService.update(model);
        OrderEntryOutputDTO orderEntryOutputDTO = orderEntryDTOModelMapper.toDTO(serviceResponse);

        kafkaOrderProducer.publish(OrderEntryEvent.builder()
                .orderEntries(orderEntryOutputDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id")
                                                  @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) Long identifier) {
        Optional<OrderEntryOutput> serviceResponse = orderEntryService.delete(identifier);
        Optional<OrderEntryOutputDTO> orderEntryOutputDTO = serviceResponse.map(orderEntryDTOModelMapper::toDTO);

        orderEntryOutputDTO.ifPresent(variantDTO -> kafkaOrderProducer.publish(OrderEntryEvent.builder()
                .orderEntries(variantDTO)
                .operationType(OperationType.DELETE)
                .build()));

        return ResponseEntity.ok()
                .build();
    }
}
