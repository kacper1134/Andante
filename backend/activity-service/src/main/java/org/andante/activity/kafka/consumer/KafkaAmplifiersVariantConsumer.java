package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.amplifiers.event.AmplifierVariantEvent;
import org.andante.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Amplifier.Variant.json", groupId = "activity.product.group", containerFactory = "containerAmplifierVariantFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaAmplifiersVariantConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload AmplifierVariantEvent amplifierVariantEvent) {
        Activity activity = toActivity(key, amplifierVariantEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, AmplifierVariantEvent amplifierVariantEvent) {
        Set<String> observers = amplifierVariantEvent.getAmplifierVariant().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(amplifierVariantEvent))
                .priority(getPriority(amplifierVariantEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(amplifierVariantEvent.getAmplifierVariant().getAmplifiersId().toString())
                .build();
    }

    private String buildMessage(AmplifierVariantEvent amplifierVariantEvent) {
        switch(amplifierVariantEvent.getOperationType()) {
            case CREATE:
                return String.format("Product %s have received a new variant. Be sure to check it out!",
                        amplifierVariantEvent.getAmplifierVariant().getProductName());
            case MODIFY:
                return String.format("Variant of product %s have been modified.", amplifierVariantEvent.getAmplifierVariant().getProductName());
            default:
                return String.format("Variant of product %s have been deleted.", amplifierVariantEvent.getAmplifierVariant().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
