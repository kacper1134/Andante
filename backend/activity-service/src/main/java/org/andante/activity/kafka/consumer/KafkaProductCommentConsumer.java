package org.andante.activity.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.andante.activity.enums.Domain;
import org.andante.activity.enums.Priority;
import org.andante.activity.logic.ActivityService;
import org.andante.activity.logic.model.Activity;
import org.andante.enums.OperationType;
import org.andante.product.event.CommentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@KafkaListener(topics = "event.Internal.Product.Comment.json", groupId = "activity.product.group", containerFactory = "containerProductCommentFactory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaProductCommentConsumer {

    private final ActivityService activityService;

    @KafkaHandler
    public void listen(@Header(KafkaHeaders.RECEIVED_KEY) String key, @Payload CommentEvent commentEvent) {
        Activity activity = toActivity(key, commentEvent);

        activityService.create(activity);
    }

    private Activity toActivity(String key, CommentEvent commentEvent) {
        Set<String> affectedUsers = commentEvent.getComment().getObservers();

        return Activity.builder()
                .key(key)
                .affectedUsers(affectedUsers)
                .description(buildMessage(commentEvent))
                .priority(getPriority(commentEvent.getOperationType()))
                .domain(Domain.PRODUCT)
                .relatedId(commentEvent.getComment().getProductId().toString())
                .affectsAll(commentEvent.getOperationType() == OperationType.CREATE)
                .build();
    }

    private String buildMessage(CommentEvent commentEvent) {
        switch (commentEvent.getOperationType()) {
            case CREATE:
                return String.format("%s added a comment for a product %s you are observing!", commentEvent.getComment().getUsername(),
                        commentEvent.getComment().getProductName());
            case MODIFY:
                return String.format("%s modified a comment for a product %s you are observing!", commentEvent.getComment().getUsername(),
                        commentEvent.getComment().getProductName());
            default:
                return String.format("%s deleted a comment for a product %s you are observing!.", commentEvent.getComment().getUsername(),
                        commentEvent.getComment().getProductName());
        }
    }

    private Priority getPriority(OperationType operationType) {
        return operationType == OperationType.CREATE ? Priority.HIGH : Priority.MEDIUM;
    }
}
