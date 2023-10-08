package org.andante.activity.configuration;

import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.util.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivityConfiguration {

    @Value("${recombee.database_id}")
    private String databaseId;

    @Value("${recombee.private_token}")
    private String privateToken;

    @Bean
    public RecombeeClient recombeeClient() {
        return new RecombeeClient(databaseId, privateToken).setRegion(Region.EU_WEST);
    }
}
