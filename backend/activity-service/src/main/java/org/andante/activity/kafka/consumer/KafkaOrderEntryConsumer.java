package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.orders.event.OrderEntryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Order.OrderEntry.json", groupId = "activity.order.group", containerFactory = "containerOrderEntryFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaOrderEntryConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderEntryEvent orderEntryEvent) {
        Activity activity = toActivity(key, orderEntryEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, OrderEntryEvent orderEntryEvent) {
        Set<String> observers = Set.of(orderEntryEvent.getOrderEntries().getOrder().getClient().getEmailAddress());
        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(orderEntryEvent))
                .priority(getPriority())
                .domain(Domain.ORDER)
                .relatedId(String.valueOf(orderEntryEvent.getOrderEntries().getId()))
                .build();
    }

    private String buildMessage(OrderEntryEvent orderEntryEvent) {
        switch (orderEntryEvent.getOperationType()) {
            case CREATE:
                return String.format("%s have been successfully added to order %d", orderEntryEvent.getOrderEntries().getProductVariant().getProductName(),
                        orderEntryEvent.getOrderEntries().getOrder().getId());
            case MODIFY:
                return String.format("Your order %d have been successfully modified", orderEntryEvent.getOrderEntries().getOrder().getId());
            default:
                return String.format("%s have been successfully deleted from order %d", orderEntryEvent.getOrderEntries().getProductVariant().getProductName(),
                        orderEntryEvent.getOrderEntries().getOrder().getId());
        }
    }

    private Priority getPriority() {
        return Priority.HIGH;
    }
}