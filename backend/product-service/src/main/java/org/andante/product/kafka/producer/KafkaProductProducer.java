package org.andante.product.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.product.event.CommentEvent;
import org.andante.product.event.ProducerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaProductProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, CommentEvent> kafkaCommentEventTemplate;
    private final KafkaTemplate<String, ProducerEvent> kafkaProducerVariantEventTemplate;

    public void publish(CommentEvent commentEvent) {
        kafkaCommentEventTemplate.send(KafkaTopic.PRODUCT_COMMENT_INTERNAL_TOPIC.getTopicName(),
                buildCommentEventKey(commentEvent), commentEvent);
    }

    public void publish(ProducerEvent producerEvent) {
        kafkaProducerVariantEventTemplate.send(KafkaTopic.PRODUCT_PRODUCER_INTERNAL_TOPIC.getTopicName(),
                buildProducerEventKey(producerEvent), producerEvent);
    }

    private String buildCommentEventKey(CommentEvent commentEvent) {
        return commentEvent.getComment().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildProducerEventKey(ProducerEvent producerEvent) {
        return producerEvent.getProducer().getName() + SEPARATOR + UUID.randomUUID();
    }
}
