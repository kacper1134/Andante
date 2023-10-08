package org.andante.subwoofers.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaSubwoofersTopicConfig {

    @Bean
    public NewTopic subwoofersTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_SUBWOOFERS_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic subwoofersVariantTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_SUBWOOFERS_VARIANT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
