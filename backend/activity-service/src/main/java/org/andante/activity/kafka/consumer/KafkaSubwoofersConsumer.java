package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.subwoofers.event.SubwoofersEvent;
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
public class KafkaSubwoofersConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload SubwoofersEvent subwoofersEvent) {
        Activity activity = toActivity(key, subwoofersEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, SubwoofersEvent subwoofersEvent) {
        Set<String> affectedUsers = subwoofersEvent.getSubwoofers().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(subwoofersEvent))
                .priority(getPriority(subwoofersEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(subwoofersEvent.getSubwoofers().getId().toString())
                .affectsAll(subwoofersEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(SubwoofersEvent subwoofersEvent) {
        switch (subwoofersEvent.getOperationType()) {
            case CREATE:
               return String.format("%s have just released %s. Be sure to check it out!", subwoofersEvent.getSubwoofers().getProducer().getName(),
                       subwoofersEvent.getSubwoofers().getName());
            case MODIFY:
                return String.format("%s have been modified", subwoofersEvent.getSubwoofers().getName());
            default:
                return String.format("%s have been removed from the store.", subwoofersEvent.getSubwoofers().getName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.MODIFY ? Priority.HIGH : Priority.MEDIUM;
    }
}
