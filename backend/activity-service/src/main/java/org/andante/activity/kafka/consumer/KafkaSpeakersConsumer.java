package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.speakers.event.SpeakersEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Speakers.json", groupId = "activity.product.group", containerFactory = "containerSpeakersFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaSpeakersConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload SpeakersEvent speakersEvent) {
        Activity activity = toActivity(key, speakersEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, SpeakersEvent speakersEvent) {
        Set<String> affectedUsers = speakersEvent.getSpeakers().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(speakersEvent))
                .priority(getPriority(speakersEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(speakersEvent.getSpeakers().getId().toString())
                .affectsAll(speakersEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(SpeakersEvent speakersEvent) {
        switch (speakersEvent.getOperationType()) {
            case CREATE:
               return String.format("%s have just released %s. Be sure to check it out!", speakersEvent.getSpeakers().getProducer().getName(),
                       speakersEvent.getSpeakers().getName());
            case MODIFY:
                return String.format("%s have been modified", speakersEvent.getSpeakers().getName());
            default:
                return String.format("%s have been removed from the store.", speakersEvent.getSpeakers().getName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.MODIFY ? Priority.HIGH : Priority.MEDIUM;
    }
}
