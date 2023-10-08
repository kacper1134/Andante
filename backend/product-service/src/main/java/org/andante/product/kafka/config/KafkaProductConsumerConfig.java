package org.andante.product.kafka.config;

import org.andante.enums.KafkaConsumerGroup;
import org.andante.orders.event.ProductOrderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaProductConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ProductOrderEvent>> containerProductOrderFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductOrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerProductOrderFactory());

        return factory;
    }

    private ConsumerFactory<String, ProductOrderEvent> consumerProductOrderFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfiguration(), new StringDeserializer(), new JsonDeserializer<>(ProductOrderEvent.class));
    }

    private Map<String, Object> getConsumerConfiguration() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, KafkaConsumerGroup.PRODUCT_ORDER_GROUP,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "org.andante.*");
    }
}
