package org.andante.headphones.kafka.config;

import org.andante.enums.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaHeadphonesTopicConfig {

    @Bean
    public NewTopic headphonesTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_HEADPHONES_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic headphonesVariantTopic() {
        return TopicBuilder.name(KafkaTopic.PRODUCT_HEADPHONES_VARIANT_INTERNAL_TOPIC.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
