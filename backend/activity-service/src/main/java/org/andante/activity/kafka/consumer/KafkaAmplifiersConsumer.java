package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.RecommendationService;
import org.andante.activity.logic.model.Activity;
import org.andante.amplifiers.event.AmplifierEvent;
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
@KafkaListener(topics = "event.Internal.Product.Amplifier.json", groupId = "activity.product.group", containerFactory = "containerAmplifierFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaAmplifiersConsumer {

    private final ActivityService activityService;
    private final RecommendationService recommendationService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload AmplifierEvent amplifierEvent) {
        Activity activity = toActivity(key, amplifierEvent);
        recommendationService.synchronizeProductsCatalog(amplifierEvent.getAmplifiers(), amplifierEvent.getOperationType());
        activityService.create(activity);
    }

    private Activity toActivity(String key, AmplifierEvent amplifierEvent) {
        Set<String> affectedUsers = amplifierEvent.getAmplifiers().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(amplifierEvent))
                .priority(getPriority(amplifierEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(amplifierEvent.getAmplifiers().getId().toString())
                .affectsAll(amplifierEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(AmplifierEvent amplifierEvent) {
        switch (amplifierEvent.getOperationType()) {
            case CREATE:
               return String.format("%s have just released %s. Be sure to check it out!", amplifierEvent.getAmplifiers().getProducer().getName(),
                       amplifierEvent.getAmplifiers().getName());
            case MODIFY:
                return String.format("%s have been modified", amplifierEvent.getAmplifiers().getName());
            default:
                return String.format("%s have been removed from the store.", amplifierEvent.getAmplifiers().getName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.MODIFY ? Priority.HIGH : Priority.MEDIUM;
    }
}
