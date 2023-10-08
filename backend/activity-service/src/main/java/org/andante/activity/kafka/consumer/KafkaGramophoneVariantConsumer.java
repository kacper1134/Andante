package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.gramophones.event.GramophoneVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Gramophone.Variant.json", groupId = "activity.product.group", containerFactory = "containerGramophoneVariantFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaGramophoneVariantConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload GramophoneVariantEvent gramophoneVariantEvent) {
        Activity activity = toActivity(key, gramophoneVariantEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, GramophoneVariantEvent gramophoneVariantEvent) {
        Set<String> observers = gramophoneVariantEvent.getGramophoneVariant().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(gramophoneVariantEvent))
                .priority(getPriority(gramophoneVariantEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(gramophoneVariantEvent.getGramophoneVariant().getGramophonesId().toString())
                .build();
    }

    private String buildMessage(GramophoneVariantEvent gramophoneVariantEvent) {
        switch(gramophoneVariantEvent.getOperationType()) {
            case CREATE:
                return String.format("Product %s have received a new variant. Be sure to check it out!",
                        gramophoneVariantEvent.getGramophoneVariant().getProductName());
            case MODIFY:
                return String.format("Variant of product %s have been modified.", gramophoneVariantEvent.getGramophoneVariant().getProductName());
            default:
                return String.format("Variant of product %s have been deleted.", gramophoneVariantEvent.getGramophoneVariant().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
