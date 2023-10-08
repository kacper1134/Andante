package org.andante.product.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.orders.event.ProductOrderEvent;
import org.andante.product.logic.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "event.Internal.Product.Order.json", groupId = "product.order.group", containerFactory = "containerProductOrderFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaProductOrderConsumer {

    private final ProductVariantService productVariantService;

    @KafkaHandler
    public void listen(@Payload ProductOrderEvent productOrderEvent) {
        Long variantIdentifier = productOrderEvent.getVariantId();
        Integer quantityChange = productOrderEvent.getOrderedQuantityChange();

        productVariantService.changeAvailableQuantity(variantIdentifier, quantityChange);
    }
}
