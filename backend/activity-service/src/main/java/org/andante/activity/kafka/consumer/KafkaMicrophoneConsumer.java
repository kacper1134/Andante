package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.microphones.event.MicrophoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Microphone.json", groupId = "activity.product.group", containerFactory = "containerMicrophoneFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaMicrophoneConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload MicrophoneEvent microphoneEvent) {
        Activity activity = toActivity(key, microphoneEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, MicrophoneEvent microphoneEvent) {
        Set<String> affectedUsers = microphoneEvent.getMicrophone().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(microphoneEvent))
                .priority(getPriority(microphoneEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(microphoneEvent.getMicrophone().getId().toString())
                .affectsAll(microphoneEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(MicrophoneEvent microphoneEvent) {
        switch (microphoneEvent.getOperationType()) {
            case CREATE:
               return String.format("%s have just released %s. Be sure to check it out!", microphoneEvent.getMicrophone().getProducer().getName(),
                       microphoneEvent.getMicrophone().getName());
            case MODIFY:
                return String.format("%s have been modified", microphoneEvent.getMicrophone().getName());
            default:
                return String.format("%s have been removed from the store.", microphoneEvent.getMicrophone().getName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.MODIFY ? Priority.HIGH : Priority.MEDIUM;
    }
}
