package utility;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainersExtension implements BeforeAllCallback {

    private static final Integer EUREKA_PORT = 8761;

    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withReuse(true);

    private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest");

    private static final GenericContainer<?> eurekaContainer = new GenericContainer<>("jakkoc/eureka:latest")
            .withExposedPorts(EUREKA_PORT)
            .withReuse(true);

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        postgresqlContainer.start();
        eurekaContainer.start();
        kafkaContainer.start();

        System.setProperty("spring.datasource.url", postgresqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresqlContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresqlContainer.getPassword());
        System.setProperty("eureka.client.service-url.defaultZone", String.format("http://localhost:%d/eureka", eurekaContainer.getMappedPort(EUREKA_PORT)));
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
    }
}
