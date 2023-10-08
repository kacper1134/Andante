package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.headphones.event.HeadphonesEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Headphones.json", groupId = "activity.product.group", containerFactory = "containerHeadphonesFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaHeadphonesConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload HeadphonesEvent headphonesEvent) {
        Activity activity = toActivity(key, headphonesEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, HeadphonesEvent headphonesEvent) {
        Set<String> affectedUsers = headphonesEvent.getHeadphones().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(headphonesEvent))
                .priority(getPriority(headphonesEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(headphonesEvent.getHeadphones().getId().toString())
                .affectsAll(headphonesEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(HeadphonesEvent headphonesEvent) {
        switch (headphonesEvent.getOperationType()) {
            case CREATE:
               return String.format("%s have just released %s. Be sure to check it out!", headphonesEvent.getHeadphones().getProducer().getName(),
                       headphonesEvent.getHeadphones().getName());
            case MODIFY:
                return String.format("%s have been modified", headphonesEvent.getHeadphones().getName());
            default:
                return String.format("%s have been removed from the store.", headphonesEvent.getHeadphones().getName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.MODIFY ? Priority.HIGH : Priority.MEDIUM;
    }
}
