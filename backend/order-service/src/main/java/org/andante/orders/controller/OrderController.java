package org.andante.orders.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.enums.OperationType;
import org.andante.orders.controller.email.EmailSender;
import org.andante.orders.controller.mapper.OrderDTOModelMapper;
import org.andante.orders.dto.OrderInputDTO;
import org.andante.orders.dto.OrderOutputDTO;
import org.andante.orders.dto.OrderQuerySpecification;
import org.andante.orders.enums.OrderSortingOrder;
import org.andante.orders.enums.OrderStatus;
import org.andante.orders.event.OrderEvent;
import org.andante.orders.kafka.producer.KafkaOrderProducer;
import org.andante.orders.logic.model.OrderInput;
import org.andante.orders.logic.model.OrderOutput;
import org.andante.orders.logic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/order")
@Validated
public class OrderController {

    private static final String IDENTIFIERS_ERROR_MESSAGE = "List of provided identifiers must contain have at least {min} element(s)";
    private static final String POSITIVE_IDENTIFIER_ERROR_MESSAGE = "Order identifier '${validatedValue}' must be a positive value";
    private static final String NULL_IDENTIFIER_ERROR_MESSAGE = "Order identifier must not be null";
    private static final String EMAIL_INVALID_ERROR_MESSAGE = "Provided value '${validatedValue}' is not a valid email address";
    private static final String EMAIL_NULL_ERROR_MESSAGE = "Provided client email must not be null";
    private static final String ORDER_STATUS_NULL_ERROR_MESSAGE = "Provided order status must be one of allowed values";
    private static final String PAGE_NEGATIVE_ERROR_MESSAGE = "Page number '${validatedValue}' must not be negative";
    private static final String PAGE_NULL_ERROR_MESSAGE = "Page number must not be null";
    private static final String PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE = "Page size '${validatedValue}' must be a positive value";
    private static final String PAGE_SIZE_NULL_ERROR_MESSAGE = "Page size must not be null";
    private static final String ORDER_SORTING_ORDER_ERROR_MESSAGE = "Order sorting order must be one of allowed values";

    private final OrderService orderService;
    private final OrderDTOModelMapper orderDTOModelMapper;
    private final KafkaOrderProducer kafkaOrderProducer;
    private final EmailSender emailSender;

    @GetMapping("/bulk/order")
    public ResponseEntity<Set<OrderOutputDTO>> getAllById(@Size(min = 1, message = IDENTIFIERS_ERROR_MESSAGE) @RequestParam("ids")
                                                                      List<@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                                           @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) Long> ids) {
        Set<OrderOutput> serviceResponse = orderService.getAllByIds(ids);
        Set<OrderOutputDTO> ordersFound = serviceResponse.stream()
                .map(orderDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(ordersFound);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderOutputDTO> getById(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                  @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long id) {
        Optional<OrderOutput> serviceResponse = orderService.getById(id);
        Optional<OrderOutputDTO> orderFound = serviceResponse.map(orderDTOModelMapper::toDTO);

        if (orderFound.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ResponseEntity.ok(orderFound.get());
    }

    @GetMapping("/client/{email}")
    public ResponseEntity<Page<OrderOutputDTO>> getByClientAndStatus(@Email(message = EMAIL_INVALID_ERROR_MESSAGE) @NotNull(message = EMAIL_NULL_ERROR_MESSAGE) @PathVariable("email") String email,
                                                                     @NotNull(message = ORDER_STATUS_NULL_ERROR_MESSAGE) @RequestParam("status") OrderStatus status,
                                                                     @PositiveOrZero(message = PAGE_NEGATIVE_ERROR_MESSAGE) @NotNull(message = PAGE_NULL_ERROR_MESSAGE) @RequestParam("page") Integer page,
                                                                     @Positive(message = PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE) @NotNull(message = PAGE_SIZE_NULL_ERROR_MESSAGE) @RequestParam("count") Integer count,
                                                                     @NotNull(message = ORDER_SORTING_ORDER_ERROR_MESSAGE) @RequestParam("order") OrderSortingOrder sortingOrder) {
        Page<OrderOutput> serviceResponse = orderService.getByClientAndStatus(email, status, page, count, sortingOrder);

        Page<OrderOutputDTO> clientOrders = serviceResponse.map(orderDTOModelMapper::toDTO);

        return ResponseEntity.ok(clientOrders);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<Long> create(@Valid @RequestBody OrderInputDTO ordersToCreate) {
        OrderInput model = orderDTOModelMapper.toModel(ordersToCreate);
        OrderOutput serviceResponse = orderService.create(model);
        OrderOutputDTO orderOutputDTO = orderDTOModelMapper.toDTO(serviceResponse);

        kafkaOrderProducer.publish(OrderEvent.builder()
                .orders(orderOutputDTO)
                .operationType(OperationType.CREATE)
                .build());

        return ResponseEntity.ok()
                .body(serviceResponse.getId());
    }

    @PostMapping(value = "/invoice/{id}", consumes = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<OperationStatus> sendInvoice(@PathVariable("id") Long identifier, @RequestBody InputStreamResource invoice) {
        OrderOutput serviceResponse = orderService.getById(identifier)
                        .orElseThrow();

        emailSender.sendOrderSummaryMail(serviceResponse, invoice);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping
    public ResponseEntity<OperationStatus> update(@Valid @RequestBody OrderInputDTO ordersToModify) {
        OrderInput model = orderDTOModelMapper.toModel(ordersToModify);
        OrderOutput serviceResponse = orderService.update(model);
        OrderOutputDTO orderOutputDTO = orderDTOModelMapper.toDTO(serviceResponse);

        kafkaOrderProducer.publish(OrderEvent.builder()
                .orders(orderOutputDTO)
                .operationType(OperationType.MODIFY)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(@Positive(message = POSITIVE_IDENTIFIER_ERROR_MESSAGE)
                                                  @NotNull(message = NULL_IDENTIFIER_ERROR_MESSAGE) @PathVariable("id") Long identifier) {
        OrderOutput serviceResponse = orderService.delete(identifier);
        OrderOutputDTO orderOutputDTO = orderDTOModelMapper.toDTO(serviceResponse);

        kafkaOrderProducer.publish(OrderEvent.builder()
                .orders(orderOutputDTO)
                .operationType(OperationType.DELETE)
                .build());

        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/query")
    public ResponseEntity<Page<OrderOutputDTO>> findByQuery(@Valid OrderQuerySpecification orderQuerySpecification) {
        Page<OrderOutput> serviceResponse = orderService.getByQuery(orderQuerySpecification);
        Page<OrderOutputDTO> orders = serviceResponse.map(OrderOutput::toDTO);

        return ResponseEntity.ok(orders);
    }

}
