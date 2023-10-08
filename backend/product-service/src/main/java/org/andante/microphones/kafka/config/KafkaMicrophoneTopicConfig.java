package org.andante.microphones.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaMicrophoneTopicConfig {

    @Bean
    public NewTopic microphonesTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_MICROPHONE_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic microphonesVariantTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_MICROPHONE_VARIANT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
