package org.andante.amplifiers.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.andante.product.repository.entity.ProductEntity;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AmplifiersTestConfiguration {

    @Bean("Amplifiers")
    public EasyRandom amplifiersGenerator() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.stringLengthRange(3, 20);
        parameters.collectionSizeRange(1, 20);
        parameters.excludeField(FieldPredicates.named("products").and(FieldPredicates.inClass(ProducerEntity.class)));
        parameters.excludeField(FieldPredicates.named("producer").and(FieldPredicates.inClass(ProductEntity.class)));
        parameters.excludeField(FieldPredicates.named("product").and(FieldPredicates.inClass(CommentEntity.class)));
        parameters.excludeField(FieldPredicates.named("variants").and(FieldPredicates.inClass(AmplifiersEntity.class)));
        parameters.excludeField(FieldPredicates.named("amplifiers").and(FieldPredicates.inClass(AmplifiersVariantEntity.class)));

        return new EasyRandom(parameters);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        return mapper;
    }
}
