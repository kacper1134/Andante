package org.andante.headphones.kafka.config;

import org.andante.headphones.event.HeadphonesEvent;
import org.andante.headphones.event.HeadphonesVariantEvent;
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
public class KafkaHeadphonesProducerConfiguration {

    private static final Integer KB = 1024;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, HeadphonesEvent> kafkaHeadphonesEventProducer() {
        return new KafkaTemplate<>(headphonesEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, HeadphonesVariantEvent> kafkaHeadphoneVariantsEventProducer() {
        return new KafkaTemplate<>(headphonesVariantEventProducerFactory());
    }

    private ProducerFactory<String, HeadphonesEvent> headphonesEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerEndpointConfiguration());
    }

    private ProducerFactory<String, HeadphonesVariantEvent> headphonesVariantEventProducerFactory() {
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
