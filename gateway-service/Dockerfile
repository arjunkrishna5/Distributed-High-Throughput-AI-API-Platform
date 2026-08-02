# Multi-stage Dockerfile building and running Java 21 Spring Boot Gateway service on port 8080.
# Uses Maven wrapper for reproducible JDK 21 build and lightweight Temurin runtime environment.

# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY gateway-service/pom.xml .
COPY gateway-service/src ./src
COPY proto /proto
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
