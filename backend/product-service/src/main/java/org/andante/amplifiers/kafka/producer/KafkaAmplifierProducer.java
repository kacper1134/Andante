package org.andante.amplifiers.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.event.AmplifierEvent;
import org.andante.amplifiers.event.AmplifierVariantEvent;
import org.andante.enums.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaAmplifierProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, AmplifierEvent> kafkaAmplifierEventTemplate;
    private final KafkaTemplate<String, AmplifierVariantEvent> kafkaAmplifierVariantEventTemplate;

    public void publish(AmplifierEvent amplifierEvent) {
        kafkaAmplifierEventTemplate.send(KafkaTopic.PRODUCT_AMPLIFIER_INTERNAL_TOPIC.getTopicName(),
                buildAmplifierEventKey(amplifierEvent), amplifierEvent);
    }

    public void publish(AmplifierVariantEvent amplifierVariantEvent) {
        kafkaAmplifierVariantEventTemplate.send(KafkaTopic.PRODUCT_AMPLIFIER_VARIANT_INTERNAL_TOPIC.getTopicName(),
                buildAmplifierVariantEventKey(amplifierVariantEvent), amplifierVariantEvent);
    }

    private String buildAmplifierEventKey(AmplifierEvent amplifierEvent) {
        return amplifierEvent.getAmplifiers().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildAmplifierVariantEventKey(AmplifierVariantEvent amplifierVariantEvent) {
        return amplifierVariantEvent.getAmplifierVariant().getId() + SEPARATOR + UUID.randomUUID();
    }
}
