# Use the official OpenJDK base image
FROM openjdk:17-oracle

# Metadata as described above
LABEL maintainer="kevinerik.iin@gmail.com"
LABEL version="1.0"
LABEL description="Docker image for my Spring Boot API"

# Set the current working directory inside the image
WORKDIR /app

# Copy the JAR file into the image
COPY /target/api-0.0.5-SNAPSHOT.jar /app/api-0.0.5-SNAPSHOT.jar

COPY /config/gc.json /app/credentials.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/credentials.json

# Set the command to run your application using the JAR
ENTRYPOINT ["java", "-jar", "/app/api-0.0.5-SNAPSHOT.jar"]
