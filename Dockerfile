FROM ubuntu:latest
LABEL authors="jerryuusitalo"

ENTRYPOINT ["top", "-b"]

# Use a base JDK image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy the built JAR from Gradle build
COPY build/libs/*.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]

