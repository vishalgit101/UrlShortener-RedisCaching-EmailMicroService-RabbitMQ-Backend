# Use Java 21 JRE Alpine
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy your actual JAR file
COPY target/LinkShortener-0.0.1-SNAPSHOT.jar UrlApp.jar

# Expose the port your app uses (optional, e.g., 8080)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "UrlApp.jar"]
