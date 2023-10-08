package org.andante.speakers.kafka.config;

import org.andante.speakers.event.SpeakersEvent;
import org.andante.speakers.event.SpeakersVariantEvent;
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
public class KafkaSpeakersProducerConfiguration {

    private static final Integer KB = 1024;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, SpeakersEvent> kafkaSpeakersEventProducer() {
        return new KafkaTemplate<>(speakersEventProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, SpeakersVariantEvent> kafkaSpeakersVariantsEventProducer() {
        return new KafkaTemplate<>(speakersVariantEventProducerFactory());
    }

    private ProducerFactory<String, SpeakersEvent> speakersEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerEndpointConfiguration());
    }

    private ProducerFactory<String, SpeakersVariantEvent> speakersVariantEventProducerFactory() {
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
