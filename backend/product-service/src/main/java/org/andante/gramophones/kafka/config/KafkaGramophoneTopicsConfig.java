package org.andante.gramophones.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaGramophoneTopicsConfig {

    @Bean
    public NewTopic gramophoneTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_GRAMOPHONE_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic gramophoneVariantTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_GRAMOPHONE_VARIANT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
