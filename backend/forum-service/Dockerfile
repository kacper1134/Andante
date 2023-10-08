FROM eclipse-temurin:11-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-forum.jar
ENTRYPOINT ["java","-jar","spring-forum.jar"]