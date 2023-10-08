package org.andante.rsql.configuration;

import cz.jirutka.rsql.parser.RSQLParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RSQLConfiguration {

    @Bean
    public RSQLParser rsqlParser() {
        return new RSQLParser();
    }
}
