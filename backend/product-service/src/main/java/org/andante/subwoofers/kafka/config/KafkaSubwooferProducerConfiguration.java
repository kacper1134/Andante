package org.andante.subwoofers.kafka.config;

import org.andante.subwoofers.event.SubwoofersEvent;
import org.andante.subwoofers.event.SubwoofersVariantEvent;
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
public class KafkaSubwooferProducerConfiguration {

    private static final Integer KB = 1024;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, SubwoofersEvent> kafkaSubwoofersEventProducer() {
        return new KafkaTemplate<>(subwoofersEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, SubwoofersVariantEvent> kafkaSubwoofersVariantsEventProducer() {
        return new KafkaTemplate<>(subwoofersVariantEventProducerFactory());
    }

    private ProducerFactory<String, SubwoofersEvent> subwoofersEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerEndpointConfiguration());
    }

    private ProducerFactory<String, SubwoofersVariantEvent> subwoofersVariantEventProducerFactory() {
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
