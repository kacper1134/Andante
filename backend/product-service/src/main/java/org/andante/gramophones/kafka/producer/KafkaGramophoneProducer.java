package org.andante.gramophones.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.gramophones.event.GramophoneEvent;
import org.andante.gramophones.event.GramophoneVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaGramophoneProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, GramophoneEvent> kafkaGramophoneEventTemplate;
    private final KafkaTemplate<String, GramophoneVariantEvent> kafkaGramophoneVariantEventTemplate;

    public void publish(GramophoneEvent gramophoneEvent) {
        kafkaGramophoneEventTemplate.send(KafkaTopic.PRODUCT_GRAMOPHONE_INTERNAL_TOPIC.getTopicName(),
                buildGramophoneEventKey(gramophoneEvent), gramophoneEvent);
    }

    public void publish(GramophoneVariantEvent gramophoneVariantEvent) {
        kafkaGramophoneVariantEventTemplate.send(KafkaTopic.PRODUCT_GRAMOPHONE_VARIANT_INTERNAL_TOPIC.getTopicName(),
                buildGramophoneVariantEventKey(gramophoneVariantEvent), gramophoneVariantEvent);
    }

    private String buildGramophoneEventKey(GramophoneEvent gramophoneEvent) {
        return gramophoneEvent.getGramophone().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildGramophoneVariantEventKey(GramophoneVariantEvent gramophoneVariantEvent) {
        return gramophoneVariantEvent.getGramophoneVariant().getId() + SEPARATOR + UUID.randomUUID();
    }
}
