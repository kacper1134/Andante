package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.microphones.event.MicrophoneVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Microphone.Variant.json", groupId = "activity.product.group", containerFactory = "containerMicrophoneVariantFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaMicrophoneVariantConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload MicrophoneVariantEvent microphoneVariantEvent) {
        Activity activity = toActivity(key, microphoneVariantEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, MicrophoneVariantEvent microphoneVariantEvent) {
        Set<String> observers = microphoneVariantEvent.getMicrophoneVariant().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(observers)
                .description(buildMessage(microphoneVariantEvent))
                .priority(getPriority(microphoneVariantEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(microphoneVariantEvent.getMicrophoneVariant().getMicrophoneId().toString())
                .build();
    }

    private String buildMessage(MicrophoneVariantEvent microphoneVariantEvent) {
        switch(microphoneVariantEvent.getOperationType()) {
            case CREATE:
                return String.format("Product %s have received a new variant. Be sure to check it out!",
                        microphoneVariantEvent.getMicrophoneVariant().getProductName());
            case MODIFY:
                return String.format("Variant of product %s have been modified.", microphoneVariantEvent.getMicrophoneVariant().getProductName());
            default:
                return String.format("Variant of product %s have been deleted.", microphoneVariantEvent.getMicrophoneVariant().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
