package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.speakers.event.SpeakersVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Speakers.Variant.json", groupId = "activity.product.group", containerFactory = "containerSpeakersFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaSpeakersVariantConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload SpeakersVariantEvent speakersVariantEvent) {
        Activity activity = toActivity(key, speakersVariantEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, SpeakersVariantEvent speakersVariantEvent) {
        Set<String> observers = speakersVariantEvent.getSpeakersVariant().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(speakersVariantEvent))
                .priority(getPriority(speakersVariantEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(speakersVariantEvent.getSpeakersVariant().getSpeakersId().toString())
                .build();
    }

    private String buildMessage(SpeakersVariantEvent speakersVariantEvent) {
        switch(speakersVariantEvent.getOperationType()) {
            case CREATE:
                return String.format("Product %s have received a new variant. Be sure to check it out!",
                        speakersVariantEvent.getSpeakersVariant().getProductName());
            case MODIFY:
                return String.format("Variant of product %s have been modified.", speakersVariantEvent.getSpeakersVariant().getProductName());
            default:
                return String.format("Variant of product %s have been deleted.", speakersVariantEvent.getSpeakersVariant().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
