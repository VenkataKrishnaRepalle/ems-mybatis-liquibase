FROM openjdk:21
LABEL authors="rvenkata"

# Copy the application jar file
ARG JAR_FILE=target/ems-mybatis-liquibase.jar
COPY ${JAR_FILE} ems-mybatis-liquibase.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/ems-mybatis-liquibase.jar"]