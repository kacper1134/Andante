package org.andante.amplifiers.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAmplifierTopicsConfig {

    @Bean
    public NewTopic amplifierTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_AMPLIFIER_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic amplifierVariantTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_AMPLIFIER_VARIANT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
