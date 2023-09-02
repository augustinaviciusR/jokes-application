# Joke API

This is a Spring Boot application built with Kotlin. The application is containerized using Docker and documented using OpenAPI specifications.

## Prerequisites

- JDK (Java Development Kit)
- IntelliJ IDEA or another IDE with Kotlin support
- Docker installed on your machine
- Gradle

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Cloning the Repository

```bash
git clone git@github.com:augustinaviciusR/jokes-application.git
cd jokes-application
```

### Running Locally Without Docker

To run the project without Docker, execute the following command:

```bash
./gradlew bootRun
```

Your application will be accessible at `http://localhost:8080`.

### Building the Docker Image

Then build your Docker image:

```bash
docker build -t jokes-app .
```

### Running the Docker Container

To run your Docker container, execute:

```bash
docker run -p 8080:8080 jokes-app
```

### Or use docker-compose

```bash
docker-compose up
```

Your application will now be accessible at `http://localhost:8080`.

## API Endpoints

For API details, refer to the OpenAPI documentation available under root directory joke-api.yaml

## Built With

- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
- [Kotlin](https://kotlinlang.org/) - Programming language
- [Docker](https://www.docker.com/) - Containerization platform
- [OpenAPI](https://www.openapis.org/) - API Specification

## Authors

- **Romas Augustinavicius** - Initial work - [Your GitHub Profile](https://github.com/augustinaviciusR)