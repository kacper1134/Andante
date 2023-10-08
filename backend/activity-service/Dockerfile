FROM eclipse-temurin:11-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} spring-activity.jar
#ARG CERT="keystore/tls.crt"
#COPY $CERT tls.crt
#RUN keytool -importcert -file tls.crt -alias $CERT -cacerts -storepass changeit -noprompt
ENTRYPOINT ["java","-jar","spring-activity.jar"]