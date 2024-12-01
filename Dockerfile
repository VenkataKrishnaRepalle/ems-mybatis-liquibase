FROM openjdk:17-jdk-alpine
LABEL authors="rvenkata"

# Copy the application jar file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080
EXPOSE 5430

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
