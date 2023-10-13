package org.andante.headphones.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.andante.enums.KafkaTopic;
import org.andante.headphones.event.HeadphonesEvent;
import org.andante.headphones.event.HeadphonesVariantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaHeadphonesProducer {

    private static final String SEPARATOR = "_";

    private final KafkaTemplate<String, HeadphonesEvent> kafkaHeadphonesEventTemplate;
    private final KafkaTemplate<String, HeadphonesVariantEvent> kafkaHeadphonesVariantEventTemplate;

    public void publish(HeadphonesEvent headphonesEvent) {
        kafkaHeadphonesEventTemplate.send(KafkaTopic.PRODUCT_HEADPHONES_INTERNAL_TOPIC.getTopicName(),
                buildHeadphonesEventKey(headphonesEvent), headphonesEvent);
    }

    public void publish(HeadphonesVariantEvent headphonesVariantEvent) {
        kafkaHeadphonesVariantEventTemplate.send(KafkaTopic.PRODUCT_HEADPHONES_VARIANT_INTERNAL_TOPIC.getTopicName(),
                buildHeadphonesVariantEventKey(headphonesVariantEvent), headphonesVariantEvent);
    }

    private String buildHeadphonesEventKey(HeadphonesEvent headphonesEvent) {
        return headphonesEvent.getHeadphones().getId() + SEPARATOR + UUID.randomUUID();
    }

    private String buildHeadphonesVariantEventKey(HeadphonesVariantEvent headphonesVariantEvent) {
        return headphonesVariantEvent.getHeadphonesVariant().getId() + SEPARATOR + UUID.randomUUID();
    }
}
