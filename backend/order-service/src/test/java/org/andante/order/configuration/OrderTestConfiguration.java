package org.andante.order.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.andante.orders.repository.entity.ClientEntity;
import org.andante.orders.repository.entity.LocationEntity;
import org.andante.orders.repository.entity.OrderEntity;
import org.andante.orders.repository.entity.OrderEntryEntity;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class OrderTestConfiguration {

    @Bean("Order")
    public EasyRandom orderGenerator() {
        EasyRandomParameters parameters = new EasyRandomParameters();
        parameters.stringLengthRange(3, 20);
        parameters.collectionSizeRange(1, 20);
        parameters.excludeField(FieldPredicates.named("emailAddress").and(FieldPredicates.inClass(ClientEntity.class)));
        parameters.excludeField(FieldPredicates.named("order").and(FieldPredicates.inClass(OrderEntryEntity.class)));
        parameters.excludeField(FieldPredicates.named("orderEntries").and(FieldPredicates.inClass(OrderEntity.class)));
        parameters.excludeField(FieldPredicates.named("productVariantId").and(FieldPredicates.inClass(OrderEntryEntity.class)));
        parameters.excludeField(FieldPredicates.named("orders").and(FieldPredicates.inClass(LocationEntity.class)));
        parameters.excludeField(FieldPredicates.named("deliveryOrders").and(FieldPredicates.inClass(LocationEntity.class)));
        parameters.excludeField(FieldPredicates.named("location_id").and(FieldPredicates.inClass(OrderEntity.class)));
        parameters.excludeField(FieldPredicates.named("client_email").and(FieldPredicates.inClass(OrderEntity.class)));

        return new EasyRandom(parameters);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        return mapper;
    }
}
