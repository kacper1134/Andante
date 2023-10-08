package org.andante.orders.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.orders.event.OrderEntryEvent;
import org.andante.orders.event.OrderEvent;
import org.andante.orders.event.ProductOrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaOrderProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, OrderEvent> kafkaOrderEventTemplate;
    private final KafkaTemplate<String, OrderEntryEvent> kafkaOrderEntryEventTemplate;
    private final KafkaTemplate<String, ProductOrderEvent> kafkaProductOrderEventTemplate;

    public void publish(OrderEvent orderEvent) {
        kafkaOrderEventTemplate.send(KafkaTopic.ORDER_INTERNAL_TOPIC.getTopicName(),
                buildOrderEventKey(orderEvent), orderEvent);
    }

    public void publish(OrderEntryEvent orderEntryEvent) {
        kafkaOrderEntryEventTemplate.send(KafkaTopic.ORDER_ENTRY_INTERNAL_TOPIC.getTopicName(),
                buildOrderEntryEventKey(orderEntryEvent), orderEntryEvent);
    }

    public void publish(ProductOrderEvent productOrderEvent) {
        kafkaProductOrderEventTemplate.send(KafkaTopic.PRODUCT_ORDER_INTERNAL_TOPIC.getTopicName(),
                buildProductOrderEventKey(productOrderEvent), productOrderEvent);
    }

    private String buildOrderEventKey(OrderEvent orderEvent) {
        return orderEvent.getOrders().getId() + SEPARATOR + LocalDateTime.now();
    }

    private String buildOrderEntryEventKey(OrderEntryEvent orderEntryEvent) {
        return orderEntryEvent.getOrderEntries().getId() + SEPARATOR + LocalDateTime.now();
    }

    private String buildProductOrderEventKey(ProductOrderEvent productOrderEvent) {
        return productOrderEvent.getVariantId() + SEPARATOR + LocalDateTime.now();
    }
}
