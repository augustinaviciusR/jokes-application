# Using builder pattern to build the jar artifact
FROM gradle:8.3.0-jdk17 AS build
WORKDIR /home/gradle/src
# Copy the source files
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src/
# Run Gradle build to jar artifact
RUN gradle clean build --no-daemon

FROM amazoncorretto:17-alpine-jdk
# Add a non-root user for security reasons
RUN addgroup --system app && adduser --system --group app
USER app
# Set the working directory
WORKDIR /app
# Copy the built jar file and rename it to app.jar
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
