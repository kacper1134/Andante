package org.andante.orders.controller.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.orders.controller.client.ProductClient;
import org.andante.orders.dto.OrderEntryInputDTO;
import org.andante.orders.dto.OrderEntryOutputDTO;
import org.andante.orders.exception.OrderCommunicationException;
import org.andante.orders.exception.OrderMalformedException;
import org.andante.orders.logic.model.OrderEntryInput;
import org.andante.orders.logic.model.OrderEntryOutput;
import org.andante.product.dto.ProductVariantOutputDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class OrderEntryDTOModelMapper {

    private static final String PRODUCT_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE = "Product variant with identifier %d does not exist";
    private static final String ORDER_COMMUNICATION_MESSAGE = "Something went wrong on product service";

    private final ProductClient productClient;
    private final OrderDTOModelMapper orderDTOModelMapper;

    public OrderEntryOutputDTO toDTO(OrderEntryOutput orderEntryOutput){
        ProductVariantOutputDTO productVariantOutputDTO = getProductVariant(orderEntryOutput.getProductVariantId());
        return  OrderEntryOutputDTO.builder()
                .id(orderEntryOutput.getId())
                .quantity(orderEntryOutput.getQuantity())
                .productVariant(productVariantOutputDTO)
                .order(orderDTOModelMapper.toDTO(orderEntryOutput.getOrderOutput()))
                .build();
    }

    public Set<OrderEntryOutputDTO> toDTO(Set<OrderEntryOutput> orderEntryOutputs){
        return  orderEntryOutputs.stream().map(orderEntryOutput -> {
            ProductVariantOutputDTO productVariantOutputDTO = getProductVariant(orderEntryOutput.getProductVariantId());
            return OrderEntryOutputDTO.builder()
                    .id(orderEntryOutput.getId())
                    .quantity(orderEntryOutput.getQuantity())
                    .productVariant(productVariantOutputDTO)
                    .order(orderDTOModelMapper.toDTO(orderEntryOutput.getOrderOutput()))
                    .build();
        }).collect(Collectors.toSet());
    }

    public OrderEntryInput toModel(OrderEntryInputDTO orderEntryInputDTO){
        return OrderEntryInput.builder()
                .id(orderEntryInputDTO.getIdentifier())
                .quantity(orderEntryInputDTO.getQuantity())
                .orderId(orderEntryInputDTO.getOrderId())
                .productVariantId(orderEntryInputDTO.getProductVariantId())
                .build();
    }

    public Set<OrderEntryInput> toModel(Set<OrderEntryInputDTO> orderEntryInputDTOs){
        return orderEntryInputDTOs.stream().map(orderEntryInputDTO -> OrderEntryInput.builder()
                .id(orderEntryInputDTO.getIdentifier())
                .quantity(orderEntryInputDTO.getQuantity())
                .orderId(orderEntryInputDTO.getOrderId())
                .productVariantId(orderEntryInputDTO.getProductVariantId())
                .build()).collect(Collectors.toSet());
    }

    private ProductVariantOutputDTO getProductVariant(Long id) {
        ResponseEntity<Set<ProductVariantOutputDTO>> productResponse = productClient.getVariantsByIds(Set.of(id));

        if (!productResponse.hasBody()) {
            throw new OrderCommunicationException(ORDER_COMMUNICATION_MESSAGE);
        }

        Set<ProductVariantOutputDTO> productVariants = productResponse.getBody();
        if (productVariants == null || productVariants.isEmpty()) {
            throw new OrderMalformedException(String.format(PRODUCT_VARIANT_NOT_FOUND_EXCEPTION_MESSAGE, id));
        }

        return productVariants.stream()
                .findAny()
                .get();
    }
}
