package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.gramophones.event.GramophoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Gramophone.json", groupId = "activity.product.group", containerFactory = "containerGramophoneFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaGramophonesConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload GramophoneEvent gramophoneEvent) {
        Activity activity = toActivity(key, gramophoneEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, GramophoneEvent gramophoneEvent) {
        Set<String> affectedUsers = gramophoneEvent.getGramophone().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(gramophoneEvent))
                .priority(getPriority(gramophoneEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(gramophoneEvent.getGramophone().getId().toString())
                .affectsAll(gramophoneEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(GramophoneEvent gramophoneEvent) {
        switch (gramophoneEvent.getOperationType()) {
            case CREATE:
               return String.format("%s have just released %s. Be sure to check it out!", gramophoneEvent.getGramophone().getProducer().getName(),
                       gramophoneEvent.getGramophone().getName());
            case MODIFY:
                return String.format("%s have been modified", gramophoneEvent.getGramophone().getName());
            default:
                return String.format("%s have been removed from the store.", gramophoneEvent.getGramophone().getName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.MODIFY ? Priority.HIGH : Priority.MEDIUM;
    }
}
