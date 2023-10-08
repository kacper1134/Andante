package org.andante.orders.kafka.config;

import org.andante.orders.event.OrderEntryEvent;
import org.andante.orders.event.OrderEvent;
import org.andante.orders.event.ProductOrderEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaOrderProducerConfiguration {

    private static final Integer KB = 1024;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaOrderEventProducer() {
        return new KafkaTemplate<>(orderEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, OrderEntryEvent> kafkaOrderEntryEventProducer() {
        return new KafkaTemplate<>(orderEntryEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ProductOrderEvent> kafkaProductOrderEventProducer() {
        return new KafkaTemplate<>(productOrderEventProducerFactory());
    }

    private ProducerFactory<String, OrderEvent> orderEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerEndpointConfiguration());
    }

    private ProducerFactory<String, OrderEntryEvent> orderEntryEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerEndpointConfiguration());
    }

    private ProducerFactory<String, ProductOrderEvent> productOrderEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerEndpointConfiguration());
    }

    private Map<String, Object> producerEndpointConfiguration() {
        return Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                ProducerConfig.LINGER_MS_CONFIG, "20",
                ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(32 * KB));
    }
}
