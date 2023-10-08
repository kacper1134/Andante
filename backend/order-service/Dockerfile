FROM eclipse-temurin:11-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} orders-service.jar
ENTRYPOINT ["java", "-jar", "orders-service.jar"]