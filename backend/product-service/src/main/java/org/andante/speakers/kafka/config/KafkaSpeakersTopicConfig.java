package org.andante.speakers.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaSpeakersTopicConfig {

    @Bean
    public NewTopic speakersTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_SPEAKERS_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic speakersVariantTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_SPEAKERS_VARIANT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
