package org.andante.subwoofers.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.subwoofers.event.SubwoofersEvent;
import org.andante.subwoofers.event.SubwoofersVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaSubwoofersProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, SubwoofersEvent> kafkaSubwoofersEventTemplate;
    private final KafkaTemplate<String, SubwoofersVariantEvent> kafkaSubwoofersVariantEventTemplate;

    public void publish(SubwoofersEvent subwoofersEvent) {
        kafkaSubwoofersEventTemplate.send(KafkaTopic.PRODUCT_SUBWOOFERS_INTERNAL_TOPIC.getTopicName(),
                buildSpeakersEventKey(subwoofersEvent), subwoofersEvent);
    }

    public void publish(SubwoofersVariantEvent subwoofersVariantEvent) {
        kafkaSubwoofersVariantEventTemplate.send(KafkaTopic.PRODUCT_SUBWOOFERS_VARIANT_INTERNAL_TOPIC.getTopicName(),
                buildSpeakersVariantEventKey(subwoofersVariantEvent), subwoofersVariantEvent);
    }

    private String buildSpeakersEventKey(SubwoofersEvent subwoofersEvent) {
        return subwoofersEvent.getSubwoofers().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildSpeakersVariantEventKey(SubwoofersVariantEvent subwoofersVariantEvent) {
        return subwoofersVariantEvent.getSubwoofersVariant().getId() + SEPARATOR + UUID.randomUUID();
    }
}
