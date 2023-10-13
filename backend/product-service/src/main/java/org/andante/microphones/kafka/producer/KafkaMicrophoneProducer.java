package org.andante.microphones.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.microphones.event.MicrophoneEvent;
import org.andante.microphones.event.MicrophoneVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaMicrophoneProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, MicrophoneEvent> kafkaMicrophoneEventTemplate;
    private final KafkaTemplate<String, MicrophoneVariantEvent> kafkaMicrophoneVariantEventTemplate;

    public void publish(MicrophoneEvent microphoneEvent) {
        kafkaMicrophoneEventTemplate.send(KafkaTopic.PRODUCT_MICROPHONE_INTERNAL_TOPIC.getTopicName(),
                buildMicrophoneEventKey(microphoneEvent), microphoneEvent);
    }

    public void publish(MicrophoneVariantEvent microphoneVariantEvent) {
        kafkaMicrophoneVariantEventTemplate.send(KafkaTopic.PRODUCT_MICROPHONE_VARIANT_INTERNAL_TOPIC.getTopicName(),
                buildMicrophoneVariantEventKey(microphoneVariantEvent), microphoneVariantEvent);
    }

    private String buildMicrophoneEventKey(MicrophoneEvent microphoneEvent) {
        return microphoneEvent.getMicrophone().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildMicrophoneVariantEventKey(MicrophoneVariantEvent microphoneVariantEvent) {
        return microphoneVariantEvent.getMicrophoneVariant().getId() + SEPARATOR + UUID.randomUUID();
    }
}
