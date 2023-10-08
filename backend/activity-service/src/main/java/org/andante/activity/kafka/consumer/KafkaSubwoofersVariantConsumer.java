package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.subwoofers.event.SubwoofersVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Subwoofers.Variant.json", groupId = "activity.product.group", containerFactory = "containerSubwoofersFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaSubwoofersVariantConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload SubwoofersVariantEvent subwoofersVariantEvent) {
        Activity activity = toActivity(key, subwoofersVariantEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, SubwoofersVariantEvent subwoofersVariantEvent) {
        Set<String> observers = subwoofersVariantEvent.getSubwoofersVariant().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(subwoofersVariantEvent))
                .priority(getPriority(subwoofersVariantEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(subwoofersVariantEvent.getSubwoofersVariant().getSubwoofersId().toString())
                .build();
    }

    private String buildMessage(SubwoofersVariantEvent subwoofersVariantEvent) {
        switch(subwoofersVariantEvent.getOperationType()) {
            case CREATE:
                return String.format("Product %s have received a new variant. Be sure to check it out!",
                        subwoofersVariantEvent.getSubwoofersVariant().getProductName());
            case MODIFY:
                return String.format("Variant of product %s have been modified.", subwoofersVariantEvent.getSubwoofersVariant().getProductName());
            default:
                return String.format("Variant of product %s have been deleted.", subwoofersVariantEvent.getSubwoofersVariant().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
