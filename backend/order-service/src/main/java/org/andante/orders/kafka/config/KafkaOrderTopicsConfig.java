package org.andante.orders.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrderTopicsConfig {

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(KafkaTopic.ORDER_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderEntryTopic() {
        return TopicBuilder.name(KafkaTopic.ORDER_ENTRY_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
