package org.andante.orders.logic.service.validator;

import lombok.RequiredArgsConstructor;
import org.andante.orders.controller.client.ProductClient;
import org.andante.orders.exception.OrderCommunicationException;
import org.andante.orders.exception.OrderProductViolationException;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.service.OrderService;
import org.andante.orders.repository.OrderRepository;
import org.andante.product.dto.ProductOrderVariantDTO;
import org.andante.product.dto.ProductOrderViolationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductOrderValidator {

    private static final String ORDER_COMMUNICATION_MESSAGE = "Something went wrong on product service";

    private final ProductClient productClient;


    @Transactional(propagation = Propagation.MANDATORY)
    public void validateOrder(Set<OrderEntryInput> orderEntryInputs) {
        Set<ProductOrderVariantDTO> orderVariants = orderEntryInputs.stream()
                .map(orderEntryInput -> ProductOrderVariantDTO.builder()
                        .variantIdentifier(orderEntryInput.getProductVariantId())
                        .orderedQuantity(orderEntryInput.getQuantity())
                        .build())
                .collect(Collectors.toSet());

        Set<String> violationMessages = fetchViolationMessages(orderVariants);

        if (!violationMessages.isEmpty()) {
            throw new OrderProductViolationException(violationMessages);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void validateOrder(Long variantId, Integer quantity) {
        Set<String> violationMessages = fetchViolationMessages(Set.of(ProductOrderVariantDTO.builder()
                .variantIdentifier(variantId)
                .orderedQuantity(quantity)
                .build()));

        if (!violationMessages.isEmpty()) {
            throw new OrderProductViolationException(violationMessages);
        }
    }

    private Set<String> fetchViolationMessages(Set<ProductOrderVariantDTO> productOrders) {
        ResponseEntity<Set<ProductOrderViolationDTO>> clientResponse = productClient.validateOrder(productOrders);

        if (!clientResponse.hasBody()) {
            throw new OrderCommunicationException(ORDER_COMMUNICATION_MESSAGE);
        }

        Set<ProductOrderViolationDTO> orderViolations = clientResponse.getBody();

        if (orderViolations == null) {
            throw new OrderCommunicationException(ORDER_COMMUNICATION_MESSAGE);
        }

        return orderViolations.stream()
                .map(ProductOrderViolationDTO::getMessage)
                .collect(Collectors.toSet());
    }
}
