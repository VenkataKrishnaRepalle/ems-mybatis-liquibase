FROM openjdk:17-jdk-alpine
LABEL authors="rvenkata"
ARG JAR_FILE=target/*.jar
COPY ./target/ems-mybatis-liquibase-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]