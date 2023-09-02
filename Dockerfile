FROM gradle:8.3.0-jdk17 AS build
WORKDIR /home/gradle/src
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src/
RUN gradle clean build --no-daemon

# Use Amazon Corretto 17 with Alpine Linux as the runtime base image
FROM amazoncorretto:17-alpine-jdk

# Add a non-root user
RUN addgroup -S app && adduser -S app -G app

# Switch to the non-root user
USER app

# Set the working directory to the user's home directory, where the user has write access
WORKDIR /home/app

# Copy the built jar files into the current directory
COPY --from=build --chown=app:app /home/gradle/src/build/libs/jokes-application-latest.jar ./app.jar

# Expose port 8080
EXPOSE 8080

# Set the entry point to run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
