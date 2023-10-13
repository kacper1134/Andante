package org.andante.speakers.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.speakers.event.SpeakersEvent;
import org.andante.speakers.event.SpeakersVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaSpeakersProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, SpeakersEvent> kafkaSpeakersEventTemplate;
    private final KafkaTemplate<String, SpeakersVariantEvent> kafkaSpeakersVariantEventTemplate;

    public void publish(SpeakersEvent speakersEvent) {
        kafkaSpeakersEventTemplate.send(KafkaTopic.PRODUCT_SPEAKERS_INTERNAL_TOPIC.getTopicName(),
                buildSpeakersEventKey(speakersEvent), speakersEvent);
    }

    public void publish(SpeakersVariantEvent speakersVariantEvent) {
        kafkaSpeakersVariantEventTemplate.send(KafkaTopic.PRODUCT_SPEAKERS_VARIANT_INTERNAL_TOPIC.getTopicName(),
                buildSpeakersVariantEventKey(speakersVariantEvent), speakersVariantEvent);
    }

    private String buildSpeakersEventKey(SpeakersEvent speakersEvent) {
        return speakersEvent.getSpeakers().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildSpeakersVariantEventKey(SpeakersVariantEvent speakersVariantEvent) {
        return speakersVariantEvent.getSpeakersVariant().getId() + SEPARATOR + UUID.randomUUID();
    }
}
