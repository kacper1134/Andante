package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.orders.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Order.json", groupId = "activity.order.group", containerFactory = "containerOrderFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaOrderConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload OrderEvent orderEvent) {
        Activity activity = toActivity(key, orderEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, OrderEvent orderEvent) {
        Set<String> observers = Set.of(orderEvent.getOrders().getClient().getEmailAddress());

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(orderEvent))
                .priority(getPriority())
                .domain(Domain.ORDER)
                .relatedId(String.valueOf(orderEvent.getOrders().getId()))
                .build();
    }

    private String buildMessage(OrderEvent orderEvent) {
        switch(orderEvent.getOperationType()) {
            case CREATE:
                return String.format("Your order %d have been successfully placed in the system!", orderEvent.getOrders().getId());
            case MODIFY:
                return String.format("Your order %d have been successfully modified", orderEvent.getOrders().getId());
            default:
                return String.format("Your order %d have been successfully canceled!", orderEvent.getOrders().getId());
        }
    }

    private Priority getPriority() {
        return Priority.HIGHEST;
    }
}
