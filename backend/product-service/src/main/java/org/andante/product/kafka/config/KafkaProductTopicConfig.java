package org.andante.product.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProductTopicConfig {

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_COMMENT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic producerTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_PRODUCER_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productOrderTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_ORDER_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
