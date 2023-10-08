package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.headphones.event.HeadphonesVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Headphones.Variant.json", groupId = "activity.product.group", containerFactory = "containerHeadphonesVariantFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaHeadphonesVariantConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload HeadphonesVariantEvent headphonesVariantEvent) {
        Activity activity = toActivity(key, headphonesVariantEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, HeadphonesVariantEvent headphonesVariantEvent) {
        Set<String> observers = headphonesVariantEvent.getHeadphonesVariant().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(headphonesVariantEvent))
                .priority(getPriority(headphonesVariantEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(headphonesVariantEvent.getHeadphonesVariant().getHeadphonesId().toString())
                .build();
    }

    private String buildMessage(HeadphonesVariantEvent headphonesVariantEvent) {
        switch(headphonesVariantEvent.getOperationType()) {
            case CREATE:
                return String.format("Product %s have received a new variant. Be sure to check it out!",
                        headphonesVariantEvent.getHeadphonesVariant().getProductName());
            case MODIFY:
                return String.format("Variant of product %s have been modified.", headphonesVariantEvent.getHeadphonesVariant().getProductName());
            default:
                return String.format("Variant of product %s have been deleted.", headphonesVariantEvent.getHeadphonesVariant().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
