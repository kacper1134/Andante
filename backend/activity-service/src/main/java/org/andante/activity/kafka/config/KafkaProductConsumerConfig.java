package org.andante.activity.kafka.config;

import org.andante.amplifiers.event.AmplifierEvent;
import org.andante.amplifiers.event.AmplifierVariantEvent;
import org.andante.enums.KafkaConsumerGroup;
import org.andante.gramophones.event.GramophoneEvent;
import org.andante.gramophones.event.GramophoneVariantEvent;
import org.andante.headphones.event.HeadphonesEvent;
import org.andante.headphones.event.HeadphonesVariantEvent;
import org.andante.microphones.event.MicrophoneEvent;
import org.andante.microphones.event.MicrophoneVariantEvent;
import org.andante.orders.event.OrderEntryEvent;
import org.andante.orders.event.OrderEvent;
import org.andante.product.event.CommentEvent;
import org.andante.speakers.event.SpeakersEvent;
import org.andante.speakers.event.SpeakersVariantEvent;
import org.andante.subwoofers.event.SubwoofersEvent;
import org.andante.subwoofers.event.SubwoofersVariantEvent;
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
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, AmplifierEvent>> containerAmplifierFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AmplifierEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerAmplifiersFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, AmplifierVariantEvent>> containerAmplifierVariantFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AmplifierVariantEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerAmplifiersVariantFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, GramophoneEvent>> containerGramophoneFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GramophoneEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerGramophonesFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, GramophoneVariantEvent>> containerGramophoneVariantFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GramophoneVariantEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerGramophonesVariantFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, HeadphonesEvent>> containerHeadphonesFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HeadphonesEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerHeadphonesFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, HeadphonesVariantEvent>> containerHeadphonesVariantFactory() {
        ConcurrentKafkaListenerContainerFactory<String, HeadphonesVariantEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerHeadphonesVariantFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MicrophoneEvent>> containerMicrophoneFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MicrophoneEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerMicrophonesFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, MicrophoneVariantEvent>> containerMicrophoneVariantFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MicrophoneVariantEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerMicrophonesVariantFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SpeakersEvent>> containerSpeakersFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SpeakersEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerSpeakersFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SpeakersVariantEvent>> containerSpeakersVariantFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SpeakersVariantEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerSpeakersVariantFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SubwoofersEvent>> containerSubwoofersFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SubwoofersEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerSubwoofersFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SubwoofersVariantEvent>> containerSubwoofersVariantFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SubwoofersVariantEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerSubwoofersVariantFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, CommentEvent>> containerProductCommentFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CommentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerCommentFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderEvent>> containerOrderFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOrderFactory());

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderEntryEvent>> containerOrderEntryFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEntryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerOrderEntryFactory());

        return factory;
    }

    private ConsumerFactory<String, AmplifierVariantEvent> consumerAmplifiersVariantFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(AmplifierVariantEvent.class));
    }

    private ConsumerFactory<String, AmplifierEvent> consumerAmplifiersFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(AmplifierEvent.class));
    }

    private ConsumerFactory<String, GramophoneVariantEvent> consumerGramophonesVariantFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(GramophoneVariantEvent.class));
    }

    private ConsumerFactory<String, GramophoneEvent> consumerGramophonesFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(GramophoneEvent.class));
    }

    private ConsumerFactory<String, HeadphonesVariantEvent> consumerHeadphonesVariantFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(HeadphonesVariantEvent.class));
    }

    private ConsumerFactory<String, HeadphonesEvent> consumerHeadphonesFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(HeadphonesEvent.class));
    }

    private ConsumerFactory<String, MicrophoneVariantEvent> consumerMicrophonesVariantFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(MicrophoneVariantEvent.class));
    }

    private ConsumerFactory<String, MicrophoneEvent> consumerMicrophonesFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(MicrophoneEvent.class));
    }

    private ConsumerFactory<String, SpeakersVariantEvent> consumerSpeakersVariantFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(SpeakersVariantEvent.class));
    }

    private ConsumerFactory<String, SpeakersEvent> consumerSpeakersFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(SpeakersEvent.class));
    }

    private ConsumerFactory<String, SubwoofersVariantEvent> consumerSubwoofersVariantFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(SubwoofersVariantEvent.class));
    }

    private ConsumerFactory<String, SubwoofersEvent> consumerSubwoofersFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(SubwoofersEvent.class));
    }

    private ConsumerFactory<String, CommentEvent> consumerCommentFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(CommentEvent.class));
    }

    private ConsumerFactory<String, OrderEvent> consumerOrderFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(OrderEvent.class));
    }

    private ConsumerFactory<String, OrderEntryEvent> consumerOrderEntryFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new StringDeserializer(), new JsonDeserializer<>(OrderEntryEvent.class));
    }

    private Map<String, Object> consumerConfig() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, KafkaConsumerGroup.ACTIVITY_PRODUCT_GROUP.getName(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    }
}
