FROM eclipse-temurin:11-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-product.jar
ENTRYPOINT ["java", "-jar", "spring-product.jar"]